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
  : LifecycleComponent<S>(),
    DefaultLifecycleObserver {

  private val viewEventPropagatorLiveData = MutableLiveData<E>()

  /**
   * Delegate view events to additional presenters.
   */
  fun viewEventDelegator(presenter: BasePresenter<*, E>) {
    viewEventPropagatorLiveData.observe(lifecycleOwner!!, Observer { presenter.onViewEvent(it) })
  }

  /**
   * Propagate states sent by this presenter to another observer.
   * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
   */
  fun propagateStatesTo(observer: (S) -> Unit) {
    lifecycleOwner ?: error("You must call bind() prior to propagating states. Alternatively you must provide a lifecycle.")
    stateLiveData().observe(lifecycleOwner!!, Observer { observer(it) })
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
}