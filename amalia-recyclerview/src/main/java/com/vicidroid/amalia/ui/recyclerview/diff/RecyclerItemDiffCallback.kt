package com.vicidroid.amalia.ui.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil
import com.vicidroid.amalia.ui.recyclerview.RecyclerItem

/**
 * Generic implementation of DiffUtil.Callback focused on classes extending [RecyclerItem]
 *
 * Assumes [RecyclerItem.diffItem] is overridden to know what item to diff
 */
class RecyclerItemDiffCallback<T : RecyclerItem<*>>(val oldItems: List<T>, val newItems: List<T>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition].diffItem.diffId == newItems[newItemPosition].diffItem.diffId

    override fun getOldListSize() =
        oldItems.size

    override fun getNewListSize() =
        newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition].diffItem == newItems[newItemPosition].diffItem

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}