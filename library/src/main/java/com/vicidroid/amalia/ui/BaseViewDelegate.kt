package com.vicidroid.amalia.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.vicidroid.amalia.R
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState

abstract class BaseViewDelegate<S : ViewState, E : ViewEvent>(
    val lifecycleOwner: LifecycleOwner,
    val rootView: View,
    injectLayoutId: Int? = null,
    rootViewAnchorId: Int = R.id.amalia_stub)
  : LifecycleOwner {

  val context: Context = rootView.context

  init {
    injectLayoutId?.let { layoutId ->
      findViewById<ViewStub>(rootViewAnchorId).apply {
        layoutResource = layoutId
        inflate()
      }
    }
  }

  /**
   * Emits view events that should be processed by #onEvent from a presenter.
   */
  private val eventLiveData = MutableLiveData<E>()

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
  fun eventLiveData(): LiveData<E> = eventLiveData

  /**
   * Allows view delegates to know about events that are sent.
   * Parent view delegates may use this to intercept events sent from child delegates.
   */
  fun propagateEventsTo(observer: (E) -> Unit) =
      eventLiveData().observe(lifecycleOwner, Observer { observer(it) })

  /**
   * Exposes the lifecycle from the [lifecycleOwner]
   */
  override fun getLifecycle() = lifecycleOwner.lifecycle

  /**
   * Sends event from some interaction or UI change to an active subscriber (Presenter)
   */
  fun pushEvent(event: E) {
    eventLiveData.value = event
  }

  /**
   * Render a view state that is provided by the Presenter.
   * The view delegate updates the UI accordingly.
   */
  abstract fun renderViewState(state: S)

  fun toast(id: Int) {
    Toast.makeText(context, id, Toast.LENGTH_LONG).show()
  }

  /**
   * View delegates should handle [onSaveInstanceState] to ensure compliance with process death.
   * Ensure [onSaveInstanceState] is called by the hosting fragment or activity
   * TODO: build hook to automatically call [onSaveInstanceState]
   */
  abstract fun onSaveInstanceState(outState: Bundle)

}