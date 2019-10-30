package com.vicidroid.amalia.ui.recyclerview.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem

class AsyncRecyclerItemDiffCallback : DiffUtil.ItemCallback<RecyclerItem>() {
    override fun areItemsTheSame(oldItem: RecyclerItem, newItem: RecyclerItem) =
        oldItem.diffItem.diffId == newItem.diffItem.diffId

    //We expect kotlin data classes to implement equals
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: RecyclerItem, newItem: RecyclerItem) =
        oldItem.diffItem == newItem.diffItem

    override fun getChangePayload(oldItem: RecyclerItem, newItem: RecyclerItem): Any? {
        return ChangePayload(oldItem, newItem)
    }
}