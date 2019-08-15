package com.vicidroid.amalia.ui.recyclerview.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEventStore
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.recyclerview.*
import com.vicidroid.amalia.ui.recyclerview.diff.AsyncRecyclerItemDiffCallback
import com.vicidroid.amalia.ui.recyclerview.diff.RecyclerItemDiffCallback

class DefaultRecyclerViewAdapter<I : RecyclerItem<VH>, VH : BaseRecyclerViewHolder>(
    override val lifecycleOwner: LifecycleOwner,
    override val viewDelegate: RecyclerViewDelegate<*, *>,
    asyncDiffCallback: DiffUtil.ItemCallback<I>
) :
    RecyclerView.Adapter<VH>(), RecyclerViewAdapter {

    val viewHolderEventStore = ViewEventStore<RecyclerViewHolderInteractionEvent>()
    private val asyncListDiffer = AsyncListDiffer(this, asyncDiffCallback)

    /**
     * A list of [RecyclerItem] which wraps the data and allows bind and unbind calls.
     * Only used if [USE_ASYNC_LIST_DIFFER] is false
     */
    private var items: List<I> = mutableListOf()

    val adapterItems: List<I>
        get() = if (USE_ASYNC_LIST_DIFFER) asyncListDiffer.currentList else items

    /**
     * Match view types to a given view item for easy creation of the [RecyclerView.ViewHolder].
     * Otherwise we would have to search each item in [adapterItems] and compare viewtypes
     *
     * Warning: This will only store one item even if there are multiple items for one viewtype.
     */
    private val viewTypeToItemCache = SparseArray<I>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        recyclerViewDebugLog("onCreateViewHolder(): viewType=$viewType")
        return viewTypeToItemCache[viewType].let { item ->
            val view = item.inflateView(parent)

            item.createViewHolder(view).also {
                it.provideExtras(item, this, viewHolderEventStore)
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        recyclerViewDebugLog("onBindViewHolder() with payloads: position=$position")
        adapterItems[position].let { item ->
            item.bind(holder, payloads)
            holder.adapterItem = item
        }
    }

    /**
     * It is possible for the position to be -1 if an item is removed.
     * Otherwise, it is expected to have this called as the views go off screen
     */
    override fun onViewRecycled(holder: VH) {
        recyclerViewDebugLog("onViewRecycled() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..adapterItems.size) {
            adapterItems[holder.adapterPosition].unbind(holder)
            holder.adapterItem = null
        }
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        recyclerViewDebugLog("onFailedToRecycleView() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..adapterItems.size) {
            adapterItems[holder.adapterPosition].unbind(holder)
        }

        holder.adapterItem = null

        return super.onFailedToRecycleView(holder)
    }

    override fun getItemCount() = adapterItems.size

    override fun getItemId(position: Int) = adapterItems[position].uniqueItemId

    override fun getItemViewType(position: Int) = adapterItems[position].viewType

    private fun calculateDiff(oldItems: List<I>, newItems: List<I>) =
        DiffUtil.calculateDiff(
            RecyclerItemDiffCallback(
                oldItems,
                newItems
            )
        )

    fun update(newItems: List<I>) {
        recyclerViewDebugLog("update(): newItems.size() = ${newItems.size}")

        if (!USE_ASYNC_LIST_DIFFER) {
            val diff = calculateDiff(adapterItems, newItems)
            items = newItems

            diff.dispatchUpdatesTo(this)
            //TODO page registry so we can automatically add viewtypes for flows in a unique way.
            // This will solve the problem of having two pages with the same layout but represented by different viewholders.
        } else {
            updateAsync(newItems)
        }

        cacheViewTypeToItem(newItems)
    }

    private fun updateAsync(newItems: List<I>) {
        recyclerViewDebugLog("updateAsync(): newItems.size() = ${newItems.size}")
        asyncListDiffer.submitList(newItems)
        cacheViewTypeToItem(adapterItems)
    }

    private fun cacheViewTypeToItem(items: List<I>) {
        recyclerViewDebugLog("cacheViewTypeToItem()")

        viewTypeToItemCache.clear()
        items.distinctBy { it.viewType }.forEach { i -> viewTypeToItemCache.append(i.viewType, i) }
    }

    companion object {
        const val USE_ASYNC_LIST_DIFFER = true
    }
}