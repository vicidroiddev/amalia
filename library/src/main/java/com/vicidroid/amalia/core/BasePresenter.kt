package com.vicidroid.amalia.core

import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.vicidroid.amalia.ui.BaseViewDelegate

/**
 * Backed by Android's ViewModel in order to easily survive configuration changes.
 */
abstract class BasePresenter<S : ViewState, E : ViewEvent>
  : ViewModel(),
    DefaultLifecycleObserver {

  lateinit var applicationContext: Context

  private val viewStateLiveData = MutableLiveData<S>()

  private val viewEventPropagatorLiveData = MutableLiveData<E>()

  /**
   * The lifecycle owner belonging to the view delegate.
   * Note: This may differ from the lifecycle owner that is used to retain this presenter
   * - especially for fragments where the viewLifecycleOwner should be used.
   */
  var viewDelegateLifecycleOwner: LifecycleOwner? = null

  fun stateLiveData(): LiveData<S> = viewStateLiveData

  /**
   * Propagate states sent by this presenter to another observer.
   * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
   */
  fun propagateStatesTo(observer: (S) -> Unit) {
    viewDelegateLifecycleOwner ?: error("You must call bind() prior to propagating states as the view delegates lifecycle owner is required.")
    stateLiveData().observe(viewDelegateLifecycleOwner!!, Observer { observer(it) })
  }

  /**
   * Delegate view events to additional presenters.
   */
  fun viewEventDelegator(presenter: BasePresenter<*, E>) {
    viewEventPropagatorLiveData.observe(viewDelegateLifecycleOwner!!, Observer { presenter.onViewEvent(it) })
  }

  /**
   * Sends a [state] for the view delegate to process in order to reflect UI changes.
   * This must be called from the main thread.
   */
  @UiThread
  fun pushState(state: S) {
    viewStateLiveData.value = state
  }

  /**
   * Sends a [state] for the view delegate to process in order to reflect UI changes.
   * This may be called from a background thread.
   */
  @WorkerThread
  fun pushStateOnMainLooper(state: S) {
    viewStateLiveData.postValue(state)
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
   * Binds the presenter to the view delegate which allows:
   * • event propagation from delegate to presenter
   * • state propagation from presenter to delegate
   */
  fun bind(viewDelegate: BaseViewDelegate<S, E>) {
    this.viewDelegateLifecycleOwner?.let { error("Second call to bind() is suspicious.") }

    // Allow this class to listen for lifecycle events from the view delegate.
    // Just override a lifecycle method, example #onResume()
    //TODO, wrap the events, don't expose the normal events to confuse who the lifecycle owner is
    viewDelegate.lifecycleOwner.lifecycle.addObserver(this)

    // Observe events sent from the delegate
    viewDelegate
        .eventLiveData()
        .observe(viewDelegate.lifecycleOwner, Observer { event -> processViewEvent(event) })

    // Observe states sent from this presenter and propagate them to the delegate.
    // Propagation will only occur if the delegate's lifecycle owner indicates a good state.
    // Furthermore, the observer which holds on to a delegate will be removed according to the delegate's lifecycleowner
    // This will prevent leaks
    stateLiveData()
        .observe(viewDelegate.lifecycleOwner, Observer { state -> viewDelegate.renderViewState(state) })

    // Keep track of the lifecycle owner belonging to the delegate.
    // This allows event delegation to other presenters in a heirachy.
    // This must be nulled out should the lifecycle owner of the delegate go through onDestroy
    // Remember, presenters outlive the view lifecycle.
    viewDelegateLifecycleOwner = viewDelegate.lifecycleOwner

    onBindViewDelegate(viewDelegate)
  }

  /**
   * Allows for having multiple view delegates in a hierarchy.
   * Override [onBindViewDelegate] in your parent presenter and call [bind] on your child presenters
   * [viewDelegate] represents the view delegate that is bound to this presenter.
   */
  open fun onBindViewDelegate(viewDelegate: BaseViewDelegate<S, E>) {

  }

  @CallSuper
  override fun onDestroy(owner: LifecycleOwner) {
    // When the view delegate's lifecycle owner is destroyed, let's ensure we avoid any leaking.
    viewDelegateLifecycleOwner = null
  }

  override fun onCleared() {
    //https://github.com/googlecodelabs/android-lifecycles/issues/5
    // We do not have to remove the observer to the LifecycleRegistry according to the above
    // If this changes we should clear out our observer manually.
  }
}