package com.vicidroid.amalia.ui.recyclerview

import androidx.recyclerview.widget.DiffUtil

/**
 * Generic implementation of DiffUtil.Callback
 *
 * Assumes that T represents a Kotlin data class to allow ensure equals check is exhaustive.
 */
class DiffUtilCallback<T>(val oldItems: List<T>, val newItems: List<T>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition].hashCode() == newItems[newItemPosition].hashCode()

    override fun getOldListSize() =
        oldItems.size

    override fun getNewListSize() =
        newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition] == newItems[newItemPosition]

}