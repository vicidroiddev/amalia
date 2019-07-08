package com.vicidroid.amalia.ui.recyclerview

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.ext.recyclerViewDebugLog

open class DefaultRecyclerViewAdapter<in I : RecyclerBinding<VH>, VH : BaseRecyclerViewHolder>
    : RecyclerView.Adapter<VH>() {

    private var items: List<I> = emptyList()

    /**
     * Match view types to a given view item for easy creation of the viewholder.
     * Otherwise we would have to search each item in [items] and compare viewtypes
     */
    private val viewTypeToItemCache = SparseArray<I>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        recyclerViewDebugLog("onCreateViewHolder(): viewType=$viewType")
        return viewTypeToItemCache[viewType].let { item ->
            item.createViewHolder(item.inflateView(parent))
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        recyclerViewDebugLog("onBindViewHolder(): position=$position")
        items[position].bind(holder)
    }

    override fun onViewRecycled(holder: VH) {
        recyclerViewDebugLog("onViewRecycled() / unbind(): adapterPosition=${holder.adapterPosition}")
        items[holder.adapterPosition].unbind(holder)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType

    private fun <T> calculateDiff(oldItems: List<T>, newItems: List<T>) =
        DiffUtil.calculateDiff(
            DiffUtilCallback(
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