package com.vicidroid.amalia.ui.recyclerview

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEventStore
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.recyclerview.diff.RecyclerItemDiffCallback

open class DefaultRecyclerViewAdapter<I : RecyclerItem<VH>, VH : BaseRecyclerViewHolder> :
    RecyclerView.Adapter<VH>() {

    val viewHolderEventStore = ViewEventStore<RecyclerViewHolderInteractionEvent>()

    /**
     * A list of [RecyclerItem] which wraps the data and allows bind and unbind calls
     */
    protected var items: List<I> = mutableListOf()

    /**
     * Match view types to a given view item for easy creation of the [RecyclerView.ViewHolder].
     * Otherwise we would have to search each item in [items] and compare viewtypes
     *
     * Warning: This will only store one item even if there are multiple items for one viewtype.
     */
    private val viewTypeToItemCache = SparseArray<I>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        recyclerViewDebugLog("onCreateViewHolder(): viewType=$viewType")
        return viewTypeToItemCache[viewType].let { item ->
            val view = item.inflateView(parent)

            item.createViewHolder(view).also {
                it.eventStore = viewHolderEventStore
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        recyclerViewDebugLog("onBindViewHolder(): position=$position")
        items[position].let { item ->
            item.bind(holder)
            holder.adapterItem = item
        }
    }

    /**
     * It is possible for the position to be -1 if an item is removed.
     * Otherwise, it is expected to have this called as the views go off screen
     */
    override fun onViewRecycled(holder: VH) {
        recyclerViewDebugLog("onViewRecycled() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..items.size) {
            items[holder.adapterPosition].unbind(holder)
            holder.adapterItem = null
        }
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        recyclerViewDebugLog("onFailedToRecycleView() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..items.size) {
            items[holder.adapterPosition].unbind(holder)
        }

        holder.adapterItem = null

        return super.onFailedToRecycleView(holder)
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].uniqueItemId

    override fun getItemViewType(position: Int) = items[position].viewType

    private fun calculateDiff(oldItems: List<I>, newItems: List<I>) =
        DiffUtil.calculateDiff(
            RecyclerItemDiffCallback(
                oldItems,
                newItems
            )
        )

    fun update(newItems: List<I>) {
        val diff = calculateDiff(items, newItems)
        items = newItems

        cacheViewHolderCreatorCache(items)

        diff.dispatchUpdatesTo(this)
        //TODO page registry so we can automatically add viewtypes for flows in a unique way.
        // This will solve the problem of having two pages with the same layout but represented by different viewholders.
    }

    private fun cacheViewHolderCreatorCache(items: List<I>) {
        recyclerViewDebugLog("cacheViewHolderCreatorCache()")

        viewTypeToItemCache.clear()
        items.distinctBy { it.viewType }.forEach { i -> viewTypeToItemCache.append(i.viewType, i) }
    }
}