package com.vicidroid.amalia.ui

import android.animation.LayoutTransition
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState


abstract class ListViewDelegate<ITEM, V : ViewItem, S : ViewState, E : ViewEvent>(
    lifecycleOwner: LifecycleOwner,
    rootView: View,
    @IdRes listId: Int) :
    BaseViewDelegate(lifecycleOwner, rootView) {

  var viewItems = mutableListOf<V>()

  /**
   * Lazily find the viewgroup in case our layout is inflated into a ViewStub.
   */
  val listView by lazy {
    findViewById<ViewGroup>(listId).apply {
      applyLayoutTransitions(this)
    }
  }

  private fun applyLayoutTransitions(viewGroup: ViewGroup) {
    viewGroup.layoutTransition = LayoutTransition().apply {
      disableTransitionType(LayoutTransition.DISAPPEARING)
    }
  }

  abstract fun createViewItem(item: ITEM): V

  open fun onViewItemAdded(viewItem: ViewItem) {}

  open fun onViewItemRemoved(viewItem: V, index: Int) {}

  protected fun addViewItem(item: ITEM) {
    createViewItem(item).apply { addViewItem(this) }
  }

  private fun addViewItem(viewItem: V) {
    viewItems.add(viewItem)
    listView.addView(viewItem.rootView)
    onViewItemAdded(viewItem)
  }

  protected fun removeViewItem(viewItem: V) {
    TransitionManager.beginDelayedTransition(listView, Fade())
    viewItems.indexOf(viewItem).let { index ->
      viewItems.removeAt(index)
      listView.removeViewAt(index)
      onViewItemRemoved(viewItem, index)
    }
  }

  protected fun removeAll() {
    TransitionManager.beginDelayedTransition(listView, Fade())
    viewItems.clear()
    listView.removeAllViews()
  }

  //TODO, diff util would be nice to prevent unnecessary re-inflation
  protected open fun refreshViewItems(data: List<ITEM>) {
    removeAll()
    data.forEach { addViewItem(it) }
  }

  fun handleImeAction(view: TextView, actionId: Int) =
      when (actionId) {
        EditorInfo.IME_ACTION_NEXT,
        EditorInfo.IME_ACTION_DONE,
        EditorInfo.IME_ACTION_GO -> {
          onNextViewFocus(view)
          true
        }
        else -> false
      }

  open fun onNextViewFocus(currentView: TextView) {}

  open fun onAutocompleteAction(v: V) {}

  fun updateDataFromView() {
    viewItems.forEach { it.updateDataFromView() }
  }
}

