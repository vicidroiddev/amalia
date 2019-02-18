package com.vicidroid.amalia.ui

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat


abstract class ViewItem(val rootView: View) {

  val context: Context
    get() = rootView.context

  /**
   * Update your data entities from [view] related changes.
   * This should be used instead of applying several listeners for each view items.
   * Ideally [updateDataFromView] is used for onSaveInstanceState or prior to saving your values in the database.
   */
  open fun updateDataFromView() {}

  fun getString(@StringRes stringRes: Int) = rootView.resources.getString(stringRes)

  fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(rootView.context, colorRes)

  fun <T : View> findViewById(@IdRes id: Int): T = rootView.findViewById(id)
}
