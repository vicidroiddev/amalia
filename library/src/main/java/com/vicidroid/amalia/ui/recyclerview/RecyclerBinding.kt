package com.vicidroid.amalia.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface RecyclerBinding<VH : RecyclerView.ViewHolder> {

    /**
     * Called by adapter via [RecyclerView.Adapter.onBindViewHolder]
     */
    fun bind(viewHolder: VH)

    /**
     * Called by adapter via [RecyclerView.Adapter.onViewRecycled]
     * Ensure editable data is saved prior to recycling for EditTexts or Checkboxes.
     */
    fun unbind(viewHolder: VH)

    /**
     * By default the view type for a recycler view item can be tied to the inflated layout.
     * This may be overridden if different view types are required for the same layout id.
     */
    //TODO: For flows, we should automatically set ids pages on the actual item
    val viewType: Int
        get() = layoutRes

    val layoutRes: Int

    fun createViewHolder(itemView: View): VH

    fun inflateView(parent: ViewGroup) =
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)

}

