package com.vicidroid.amalia.ui.recyclerview.adapter

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

interface ViewHolderHelper {
    val parentView: View

    fun getString(@StringRes stringRes: Int) = parentView.resources.getString(stringRes)

    fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(parentView.context, colorRes)

    fun <T : View> findViewById(@IdRes id: Int): T = parentView.findViewById(id)
}
