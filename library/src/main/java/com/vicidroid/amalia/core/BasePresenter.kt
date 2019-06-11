package com.vicidroid.amalia.core

import android.content.Context
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.vicidroid.amalia.ui.ViewDelegate

/**
 * Backed by Android's ViewModel in order to easily survive configuration changes.
 */
abstract class BasePresenter<S : ViewState, E : ViewEvent>
    : ViewModel(),
    DefaultLifecycleObserver {

    lateinit var applicationContext: Context

    private val viewStateLiveData = MutableLiveData<S>()

    private val viewEventPropagatorLiveData = MutableLiveData<E>()

    lateinit var savedStateHandle: SavedStateHandle

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

    var presenterLifecycleOwner: LifecycleOwner? = null

    lateinit var viewLifecycleObserver: DefaultLifecycleObserver

    fun stateLiveData(): LiveData<S> = viewStateLiveData

    /**
     * Propagate states sent by this presenter to another observer.
     * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
     */
    fun propagateStatesTo(observer: (S) -> Unit) {
        viewLifecycleOwner
            ?: error("You must call bind() prior to propagating states as the view lifecycle owner is required.")
        stateLiveData().observe(viewLifecycleOwner!!, Observer { observer(it) })
    }

    //TODO Consider making this a protectedmethod if legacy code can implement ViewDelegate nicely
    @Deprecated(
        message = "[bindViewLifecycleOwner] will be made private/protected soon. Instead implement [ViewDelegate] interface",
        replaceWith = ReplaceWith("bind(viewDelegate)")
    )
    fun bindViewLifecycleOwner(viewLifecycleOwner: LifecycleOwner) {
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
     * Binds the presenter to the view delegate which allows:
     * • event propagation from delegate to presenter
     * • state propagation from presenter to delegate
     */
    fun bind(viewDelegate: ViewDelegate<S, E>) {
        bindViewLifecycleOwner(viewDelegate.viewDelegateLifecycleOwner)

        // Observe events sent from the delegate
        viewDelegate
            .eventLiveData()
            ?.observe(viewDelegate.viewDelegateLifecycleOwner, Observer { event -> processViewEvent(event) })

        // Observe states sent from this presenter and propagate them to the delegate.
        // Propagation will only occur if the delegate's lifecycle owner indicates a good state.
        // Furthermore, the observer which holds on to a delegate will be removed according to the delegate's lifecycleowner
        // This will prevent leaks
        stateLiveData()
            .observe(viewDelegate.viewDelegateLifecycleOwner, Observer { state -> viewDelegate.renderViewState(state) })

        onBindViewDelegate(viewDelegate)
    }

    /**
     * Delegate view events to additional presenters.
     */
    fun viewEventDelegator(presenter: BasePresenter<*, E>) {
        viewEventPropagatorLiveData.observe(viewLifecycleOwner!!, Observer { presenter.onViewEvent(it) })
    }

    /**
     * Sends a [state] for the view delegate to process in order to reflect UI changes.
     * This can be called from any thread.
     * [LiveData.postValue] will be used if called from a background thread.
     */
    fun pushState(state: S, preferCachedState: Boolean = false) {
        if (preferCachedState && stateLiveData().value?.javaClass == state.javaClass) return

        when (Looper.myLooper() == Looper.getMainLooper()) {
            true -> viewStateLiveData.value = state
            false -> viewStateLiveData.postValue(state)
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

    private fun createViewLifecycleObserver() = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            onViewCreated(owner)
        }

        override fun onResume(owner: LifecycleOwner) {
        }

        override fun onPause(owner: LifecycleOwner) {
        }

        override fun onStart(owner: LifecycleOwner) {
        }

        override fun onStop(owner: LifecycleOwner) {
        }

        override fun onDestroy(owner: LifecycleOwner) {
            // When the view delegate's lifecycle owner indicates destruction, let's ensure we avoid any leaking.
            viewLifecycleOwner = null

            // https://github.com/googlecodelabs/android-lifecycles/issues/
            // According to the above we do not need to remove the observer manually.
            // Just to be safe we are doing it here. It will be re-added after bind(...) is called
            owner.lifecycle.removeObserver(this)

            onViewDestroyed(owner)
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

    //TODO remove this in favour of [ViewDelegate]
    open fun onBindViewLifecycleOwner(owner: LifecycleOwner) {
    }

    open fun onViewCreated(owner: LifecycleOwner) {
    }

    open fun onViewDestroyed(owner: LifecycleOwner) {
    }
    //endregion

    //region PRESENTER RELATED CALLBACKS
    /**
     * Provides the save state handle upon presenter creation.
     * [onSaveStateHandleProvided] is called by the [com.vicidroid.amalia.ext.presenterProvider]
     * [onSaveStateHandleProvided] is guaranteed to be called before [onBindViewDelegate]
     * The handle may be used to retrieve data stored in the handle.
     * To store data, access [savedStateHandle] and save anything parceable as soon as possible.
     * There is no onSaveInstanceState callback.
     */
    open fun onSaveStateHandleProvided(handle: SavedStateHandle) {
    }

    /**
     * An explicit wrapper around viewmodels onCleared indication.
     * While presenters will survive configuration changes, they will be removed according to
     * to the lifecycle owner's ON_DESTROY event emitted by the instance of the activity or fragment.
     * Note this does not follow the viewlifecycleowner used for fragments.
     * See [com.vicidroid.amalia.ext.presenterProvider]
     *
     * This may be useful for removing certain callbacks that should outlive the view lifecycle.
     * Otherwise rely on [onViewDestroyed]
     */
    open fun onPresenterDestroyed() {

    }

    @CallSuper
    override fun onCleared() {
        presenterLifecycleOwner = null
        onPresenterDestroyed()
    }
    //endregion
}