package com.vicidroid.amalia.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

interface RecyclerItem {
    /**
     * Called by adapter via [RecyclerView.Adapter.onBindViewHolder]
     * [payloads] denotes the change payload when items are the same but contents differ somewhat
     */
    fun prepareBind(viewHolder: BaseRecyclerViewHolder, payloads: List<ChangePayload<DiffItem>>)

    /**
     * Called by adapter via [RecyclerView.Adapter.onViewRecycled]
     * Ensure editable data is saved prior to recycling for EditTexts or Checkboxes.
     */
    fun prepareUnbind(viewHolder: BaseRecyclerViewHolder)

    /**
     * Called by adapter via [RecyclerView.Adapter.getItemId]
     * Use an id that is unqiue to the content that is displayed.
     * Do not use position as a value.
     *
     * This will be leveraged for [RecyclerView.Adapter.setHasStableIds]
     */
    val uniqueItemId: Long

    /**
     * Return the item you wish to perform a diff on using or [AsyncRecyclerItemDiffCallback]
     */
    val diffItem: DiffItem

    /**
     * Return the unique id backing this recycler item, derived from the provided [diffItem]
     */
    val diffId: String
        get() = diffItem.diffId

    /**
     * By default the view type for a recycler view item can be tied to the inflated layout.
     * This may be overridden if different view types are required for the same layout id.
     */
    val viewType: Int
        get() = layoutRes

    val layoutRes: Int

    fun createViewHolder(itemView: View): BaseRecyclerViewHolder

    fun inflateView(parent: ViewGroup) =
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
}

