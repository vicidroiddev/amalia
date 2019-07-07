package com.vicidroid.amalia.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.vicidroid.amalia.R
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.core.viewdiff.ViewDiff
import com.vicidroid.amalia.core.viewdiff.ViewDiffProvider
import com.vicidroid.amalia.ext.DEBUG_LOGGING

abstract class BaseViewDelegate<S : ViewState, E : ViewEvent>(
    override val viewDelegateLifecycleOwner: LifecycleOwner,
    val rootView: View,
    injectLayoutId: Int? = null,
    rootViewAnchorId: Int = R.id.amalia_stub
) : LifecycleOwner, ViewDelegateLifecycleCallbacks, ViewDelegate<S,E> {

    constructor(components: DelegateComponents) : this(
        components.viewLifecycleOwner,
        components.rootView
    )

    private lateinit var viewAttachStateChangeListener: View.OnAttachStateChangeListener

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            /**
             * We call this in onPause since there is no guarantee an Activity
             * will go through onDestroy after being backgrounded and engaging in process death.
             */
            populateViewDiff()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onDestroyInternal()
        }
    }

    private var hostActivity: AppCompatActivity? = null

    val context: Context = rootView.context

    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * The parent view delegate is accessible from a child when using [com.vicidroid.amalia.ext.viewDelegateProvider]
     */
    var parent: BaseViewDelegate<*, *>? = null

    init {
        injectLayoutId?.let { layoutId ->
            findViewById<ViewStub>(rootViewAnchorId).apply {
                layoutResource = layoutId
                inflate()
            }
        }

        if (rootView is ViewGroup) {
            viewAttachStateChangeListener = createAttachStateChangeListener()
            rootView.addOnAttachStateChangeListener(viewAttachStateChangeListener)
            // Invoke the callbacks on this view delegate if there is an attached root.
            rootView.parent?.let { viewAttachStateChangeListener.onViewAttachedToWindow(rootView) }
        }
    }

    /**
     * Emits view events that should be processed by #onEvent from a presenter.
     */
    private val eventLiveData = MutableLiveData<E>()

    /**
     * Emits view diffs generally upon app going to background.
     * This is helpful to avoid pushing view events for every minor view change.
     *
     * Implement [ViewDiffProvider] in your view delegate.
     *
     * Example usage is a long form with several dynamically added edit texts (after onCreateView).
     *
     * Normally to ensure survival of user entered data during config changes or process death,
     * the presenter would need to know about every single view change as it happens to ensure persistence.
     * One could attach a textwatcher to every edit text and call pushEvent as an alternative.
     */
    private val viewDiffLiveData = MutableLiveData<ViewDiff>()

    /**
     * Convenience method which uses [rootView] as the parent for view traversal
     */
    fun <T : View> findViewById(@IdRes id: Int): T = rootView.findViewById(id)

    /**
     * Lazily attempts to find view using [rootView] as the parent for view traversal.
     * This can be used to avoid setting views immediately upon initialization of the view delegate.
     */
    fun <T : View> findViewByIdLazy(@IdRes id: Int) = lazy<T> { rootView.findViewById(id) }

    /**
     * Convenience method which uses the [context] obtained from the [rootView]
     */
    fun getString(@StringRes id: Int) = context.getString(id)

    /**
     * Exposes view event live data that the view delegate can leverage to reflect UI changes.
     */
    override fun eventLiveData(): LiveData<E> = eventLiveData

    /**
     * Allows view delegates to know about events that are sent.
     * Parent view delegates may use this to intercept events sent from child delegates.
     */
    fun propagateEventsTo(observer: (E) -> Unit) =
        eventLiveData().observe(viewDelegateLifecycleOwner, Observer { observer(it) })

    /**
     * Exposes the lifecycle from the [viewDelegateLifecycleOwner]
     */
    override fun getLifecycle() = viewDelegateLifecycleOwner.lifecycle

    /**
     * Sends event from some interaction or UI change to an active subscriber (Presenter)
     */
    fun pushEvent(event: E) {
        eventLiveData.value = event
    }

    fun toast(id: Int) {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show()
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun setHostActivity(activity: AppCompatActivity) {
        hostActivity?.let { "Duplicate setting of host activity is suspicious." }
        hostActivity = activity
    }

    /**
     * Recursively go up to the parent view delegate to determine the host activity
     * which is injected using [com.vicidroid.amalia.ext.viewDelegateProvider]]
     */
    fun hostActivity(): AppCompatActivity = when (parent) {
        null -> hostActivity!!
        else -> parent!!.hostActivity()
    }

    private fun onDestroyInternal() {
        onViewDetached()
        lifecycle.removeObserver(lifecycleObserver)
        rootView.removeOnAttachStateChangeListener(viewAttachStateChangeListener)
    }

    /**
     * Called when the view is attached to a parent view.
     * We start listening for lifecycle events when the view is attached.
     */
    override fun onViewAttached() {
        if (DEBUG_LOGGING) Log.v(this.javaClass.simpleName, "onViewAttached")
    }

    /**
     * Called when the view is removed from a parent view.
     * We stop listening for lifecycle events when the view is detached.

     * This may be useful if you are using activities and removing views from an anchor layout.
     * Recall that activities may not go through destruction when backgrounded.
     */
    override fun onViewDetached() {
        if (DEBUG_LOGGING) Log.v(this.javaClass.simpleName, "onViewDetached")
    }

    private fun createAttachStateChangeListener(): View.OnAttachStateChangeListener =
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                lifecycle.addObserver(lifecycleObserver)
                onViewAttached()
            }

            override fun onViewDetachedFromWindow(v: View) {
                lifecycle.removeObserver(lifecycleObserver)
                onViewDetached()
            }
        }

    //region VIEW DIFF
    fun viewDiffLiveData() : LiveData<ViewDiff> = viewDiffLiveData

    inline fun observeViewDiff(crossinline observer: (ViewDiff) -> Unit) {
        viewDiffLiveData().observe(viewDelegateLifecycleOwner, Observer<ViewDiff> { interaction ->
            observer(interaction)
        })
    }

    private fun populateViewDiff() {
        (this as? ViewDiffProvider)?.provideViewDiff()?.let { pushViewDiff(it) }
    }

    protected fun pushViewDiff(viewDiff : ViewDiff) {
        viewDiffLiveData.value = viewDiff
    }
    //endregion
}


