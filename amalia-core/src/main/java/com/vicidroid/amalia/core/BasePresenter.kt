package com.vicidroid.amalia.core

import android.content.Context
import android.os.Looper
import android.os.Parcelable
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.vicidroid.amalia.core.persistance.PersistableState
import com.vicidroid.amalia.core.viewdiff.ViewDiff
import com.vicidroid.amalia.ext.debugLog
import com.vicidroid.amalia.ext.presenterDebugLog
import com.vicidroid.amalia.ui.ViewDelegate
import com.vicidroid.amalia.ui.ViewEventProvider
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

/**
 * Backed by Android's ViewModel in order to easily survive configuration changes.
 */
abstract class BasePresenter<S : ViewState, E : ViewEvent> : ViewModel(),
    DefaultLifecycleObserver,
    PersistableState {

    val TAG_INSTANCE: String = this::class.java.simpleName

    lateinit var applicationContext: Context

    private val viewStateLiveData = MutableLiveData<S>()

    private val viewEventPropagatorLiveData = MutableLiveData<E>()

    override lateinit var savedStateHandle: SavedStateHandle

    private var viewStatePropagationPaused: Boolean = false

    /**
     * A key value cache containing objects implementing [Closeable]
     * Upon presenter destruction, close will be called on each value in this map.
     * This is useful for say coroutine scopes which should cancel when going to another screen.
     * It may be used for any other background tasks that you wish to cancel.
     */
    val closeableObjects = ConcurrentHashMap<String, Closeable>()

    /**
     * The lifecycle owner belonging to the view delegate.
     * Note: This may differ from the lifecycle owner that is used to retain this presenter
     * - especially for fragments where the viewLifecycleOwner should be used.
     */
    var viewLifecycleOwner: LifecycleOwner? = null
        private set(value) {
            field = value
            value?.let { onBindViewLifecycleOwner(it) }
        }

    internal lateinit var viewLifecycleObserver: DefaultLifecycleObserver

    fun stateLiveData(): LiveData<S> = viewStateLiveData

    val isStatePresent: Boolean
        get() = viewStateLiveData.value != null


    /**
     * Tracks child presenter invoked via [childPresenterProvider]
     * Child presenters are just objects that live in a parent presenter.
     * As such they are not added to the internal ViewModelStore (it's not necessary)
     * We should propagate [onPresenterDestroyedInternal] to these childPresenters.
     */
    val childPresenters: MutableList<BasePresenter<*, *>> = mutableListOf()

    /**
     * Propagate states sent by this presenter to another observer.
     * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
     */
    fun propagateStatesTo(observer: (S) -> Unit) {
        viewLifecycleOwner
            ?: error("You must call bind() prior to propagating states as the view lifecycle owner is required.")
        stateLiveData().observe(viewLifecycleOwner!!, Observer { observer(it) })
    }

    /**
     * Binds the presenter with a required [viewLifecycleOwner]
     * This is used internally.
     * It may be useful for legacy code where you want to move some code
     * to a presenter but most of the logic is already in a fragment or activity.
     *
     * In most cases [bind(viewDelegate)] or [bind(viewLifecycleOwner)] is preferred.
     */
    fun bind(viewLifecycleOwner: LifecycleOwner) {
        presenterDebugLog(TAG_INSTANCE, "bind(viewLifecycleOwner)")
        this.viewLifecycleOwner?.let { error("Second call to bind() is suspicious.") }
        this.viewLifecycleOwner = viewLifecycleOwner

        // Keep track of the lifecycle owner belonging to the delegate.
        // This allows event delegation to other presenters in a heirachy.
        // This must be nulled out should the lifecycle owner of the delegate go through onDestroy
        // Remember, presenters outlive the view lifecycle.
        viewLifecycleObserver = createViewLifecycleObserver()
        // Allow this class to listen for lifecycle events from the view delegate.
        // Just override a lifecycle method, example #onViewCreated()
        viewLifecycleOwner.lifecycle.addObserver(viewLifecycleObserver)
    }

    /**
     * Binds the presenter with a required [viewLifecycleOwner] and a [stateObserver]
     * States will be propagated to the [stateObserver]
     * In most cases [bind(viewDelegate)] is preferred.
     */
    fun bind(viewLifecycleOwner: LifecycleOwner, stateObserver: (S) -> Unit) {
        presenterDebugLog(TAG_INSTANCE, "bind(viewLifecycleOwner, stateObserver)")
        bind(viewLifecycleOwner)
        propagateStatesTo(stateObserver)
    }

    /**
     * Binds the presenter to the view delegate which allows:
     * • event propagation from delegate to presenter
     * • state propagation from presenter to delegate
     */
    fun bind(viewDelegate: ViewDelegate<S, E>) {
        presenterDebugLog(TAG_INSTANCE, "bind(viewDelegate)")
        bind(viewDelegate.viewDelegateLifecycleOwner)

        // Observe events sent via an event provider
        if (viewDelegate is ViewEventProvider<*>) {
            viewDelegate.propagateEventsTo { event ->
                @Suppress("UNCHECKED_CAST")
                processViewEvent(event as E)
            }
        }
        // Observe states sent from this presenter and propagate them to the delegate.
        // Propagation will only occur if the delegate's lifecycle owner indicates a good state.
        // Furthermore, the observer which holds on to a delegate will be removed according to the delegate's lifecycleowner
        // This will prevent leaks
        stateLiveData()
            .observe(
                viewDelegate.viewDelegateLifecycleOwner,
                Observer { state -> viewDelegate.renderViewState(state) })

        viewDelegate.onBindViewDelegate()
        onBindViewDelegate(viewDelegate)
    }

    /**
     * Delegate view events to additional presenters.
     */
    fun viewEventDelegator(presenter: BasePresenter<*, E>) {
        viewEventPropagatorLiveData.observe(
            viewLifecycleOwner!!,
            Observer { presenter.onViewEvent(it) })
    }

    /**
     * Sends a [state] for the view delegate to process in order to reflect UI changes.
     * This can be called from any thread.
     * [LiveData.postValue] will be used if called from a background thread.
     *
     * If you wish to persist a view state through process death ensure it is parceable.
     * Leverage [onViewStateRestored] to access the same view state if needed.
     */
    fun pushState(state: S, ignoreDuplicateState: Boolean = false) {
        if (ignoreDuplicateState && stateLiveData().value?.javaClass == state.javaClass) return

        presenterDebugLog(TAG_INSTANCE, "Pushing state: $state")

        persistViewStateIfPossible(state)

        when (Looper.myLooper() == Looper.getMainLooper()) {
            true -> viewStateLiveData.value = state
            false -> viewStateLiveData.postValue(state)
        }
    }

    @MainThread
    fun updateViewStateSilently(stateUpdater: (oldState: S) -> S) {
        val newState: S = stateLiveData().value?.let { stateUpdater(it) } ?: return

        persistViewStateIfPossible(newState)

        viewStatePropagationPaused = true
        viewStateLiveData.value = newState
        viewStatePropagationPaused = false
    }

    private fun persistViewStateIfPossible(state: S) {
        if (state is Parcelable) {
            presenterDebugLog(TAG_INSTANCE, "Persisting: $state")
            persistViewState(TAG_INSTANCE, state)
        }
    }

    private fun processViewEvent(event: E) {
        onViewEvent(event)
        viewEventPropagatorLiveData.value = event
    }

    /**
     * Process an [event] from the view delegate, and perform any business logic necessary.
     * For view changes, propagate a new state via [pushState].
     */
    open fun onViewEvent(event: E) {}

    /**
     * Allow view state to be updated without notifying observers
     * Capture view diffs from the view delegate in case the system is about to destroy the application.
     */
    open fun onViewDiffReceived(viewDiff: ViewDiff) {

    }

    private fun createViewLifecycleObserver() = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            onViewAttached(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            // When the view delegate's lifecycle owner indicates destruction, let's ensure we avoid any leaking.
            viewLifecycleOwner = null

            // https://github.com/googlecodelabs/android-lifecycles/issues/
            // According to the above we do not need to remove the observer manually.
            // Just to be safe we are doing it here. It will be re-added after bind(...) is called
            owner.lifecycle.removeObserver(this)

            onViewDetached(owner)
        }
    }

    //region VIEW RELATED CALLBACKS
    /**
     * Allows for having multiple view delegates in a hierarchy.
     * Override [onBindViewDelegate] in your parent presenter and call [bind] on your child presenters
     * [viewDelegate] represents the view delegate that is bound to this presenter.
     */
    open fun onBindViewDelegate(viewDelegate: ViewDelegate<S, E>) {
    }

    open fun onBindViewLifecycleOwner(owner: LifecycleOwner) {
    }

    open fun onViewAttached(owner: LifecycleOwner) {
    }

    open fun onViewDetached(owner: LifecycleOwner) {
    }
    //endregion

    //region PRESENTER RELATED CALLBACKS
    /**
     * Override this to perform your initial data loading.
     * This will only be called the first time the presenter instance is created.
     * It will not be called after a configuration change.
     * It will not be called after process death IFF the view state is parcelable and can be restored
     * It will be called again after process death if the view state is not parceable
     */
    open fun loadInitialState() {}

    /**
     * An explicit wrapper around viewmodel's onCleared indication.
     * While presenters will survive configuration changes, they will be removed according to
     * to the lifecycle owner's ON_DESTROY event emitted by the instance of the activity or fragment.
     * Note this does not follow the viewlifecycleowner used for fragments.
     * See [com.vicidroid.amalia.ext.presenterProvider]
     *
     * This may be useful for removing certain callbacks that should outlive the view lifecycle.
     * Otherwise rely on [onViewDetached]
     */
    open fun onPresenterDestroyed() {

    }

    internal fun onPresenterDestroyedInternal() {
        debugLog(TAG_INSTANCE, "onPresenterDestroyed()")
        onPresenterDestroyed()
        clearCloseable()
        childPresenters.forEach { it.onPresenterDestroyedInternal() }
    }

    /**
     * Note that viewmodel's onCleared is independent of configuration changes.
     */
    @CallSuper
    final override fun onCleared() {
        onPresenterDestroyedInternal()
    }

    /**
     * Calls [Closeable.close] on cached objects that may need to clean up resources when the presenter is destroyed
     */
    private fun clearCloseable() {
        closeableObjects.values.forEach { it.close() }
        closeableObjects.clear()
    }

    //endregion

    //region PERSISTABLE STATE
    /**
     * Provides the saved state [handle] & an indication of whether or not the view state has been restored
     * Generally, if the last view state was parceable, it is restored upon presenter creation.
     * [initializePresenter] is called by the [com.vicidroid.amalia.ext.presenterProvider]
     * [onViewStateRestored] is guaranteed to be called before [onBindViewDelegate]
     * The handle may be used to retrieve data persisted prior to process death.
     * To store parceable data, leverage [persist] as soon as your data changes.
     * There is no onSaveInstanceState callback.
     * Note:
     * This method prevents the need to have a savedState handle in every presenter constructor.
     * As such remember that you cannot access the handle in the constructor.
     */
    open fun onViewStateRestored(restoredViewState: S) {
    }

    fun initializePresenter(appContext: Context, handle: SavedStateHandle) {
        this.applicationContext = appContext
        this.savedStateHandle = handle

        consumePersistedOrNull<S?>(viewStateKey(TAG_INSTANCE))?.let { state ->
            presenterDebugLog(TAG_INSTANCE, "View state is being restored: $state")
            pushState(state)
            onViewStateRestored(state)
        } ?: loadInitialState()
    }

    /**
     * Saves parceable value to [savedStateHandle].
     * The handle will be injected after process death.
     * Leverage [onViewStateRestored] to get a callback when the handle has been injected after this instance is created.
     */
    override fun <V> persist(key: String, value: V) {
        savedStateHandle[key] = value
    }
    //endregion
}