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

  var lifecycleOwner: LifecycleOwner? = null

  fun stateLiveData(): LiveData<S> = viewStateLiveData

  /**
   * Propagate states sent by this presenter to another observer.
   * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
   */
  fun propagateStatesTo(observer: (S) -> Unit) {
    lifecycleOwner ?: error("You must call bind() prior to propagating states. Alternatively you must provide a lifecycle.")
    stateLiveData().observe(lifecycleOwner!!, Observer { observer(it) })
  }

  /**
   * Can be used for naked presenters that will not be attached to a view delegate.
   * This may be of use for legacy code where one wishes to separate some data loading logic
   * from a fragment and reap the benefits of presenters for lifecycle aware behaviour without
   * manipulating the view logic.
   */
  open fun propagateStatesTo(lifecycleOwner: LifecycleOwner, observer: (S) -> Unit) {
    this.lifecycleOwner = lifecycleOwner
    propagateStatesTo(observer)
  }

  /**
   * Delegate view events to additional presenters.
   */
  fun viewEventDelegator(presenter: BasePresenter<*, E>) {
    viewEventPropagatorLiveData.observe(lifecycleOwner!!, Observer { presenter.onViewEvent(it) })
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
    lifecycleOwner?.let { error("Second call to bind() is suspicious.") }

    val viewDelegateLifecycleOwner = viewDelegate.lifecycleOwner

    // Allow this class to listen for lifecycle events from the view delegate.
    // Just override a lifecycle method, example #onResume()
    viewDelegateLifecycleOwner.lifecycle.addObserver(this)

    // Observe view events sent from the view delegate
    viewDelegate
        .eventLiveData()
        .observe(viewDelegateLifecycleOwner, Observer { event -> processViewEvent(event) })

    // Propagate view states sent from this presenter
    stateLiveData()
        .observe(viewDelegateLifecycleOwner, Observer { state -> viewDelegate.renderViewState(state) })

    // Keep track of the lifecycle owner belonging to the view delegate.
    // This allows event delegation to other presenters.
    // This must be removed should the lifecycle owner go through onDestroy
    lifecycleOwner = viewDelegateLifecycleOwner

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
    lifecycleOwner = null
  }

  override fun onCleared() {
    //https://github.com/googlecodelabs/android-lifecycles/issues/5
    // We do not have to remove the observer to the LifecycleRegistry according to the above
    // If this changes we should clear out our observer manually.
  }
}