package com.vicidroid.amalia.ui.recyclerview.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem

class AsyncRecyclerItemDiffCallback<T : RecyclerItem<*>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.diffItem.diffId == newItem.diffItem.diffId

    //We expect kotlin data classes to implement equals
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem.diffItem == newItem.diffItem

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}