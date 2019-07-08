package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun getString(@StringRes stringRes: Int) = itemView.resources.getString(stringRes)

    fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(itemView.context, colorRes)
}