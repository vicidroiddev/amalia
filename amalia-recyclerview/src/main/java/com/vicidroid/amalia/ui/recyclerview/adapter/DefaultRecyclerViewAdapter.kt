package com.vicidroid.amalia.ui.recyclerview.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEventStore
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewHolderInteractionEvent
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

open class DefaultRecyclerViewAdapter(
    override val lifecycleOwner: LifecycleOwner,
    asyncDiffCallback: DiffUtil.ItemCallback<RecyclerItem>) :
    RecyclerView.Adapter<BaseRecyclerViewHolder>(), RecyclerViewAdapter {

    val viewHolderEventStore = ViewEventStore<RecyclerViewHolderInteractionEvent>()
    private val asyncListDiffer = AsyncListDiffer(this, asyncDiffCallback)

    val adapterItems: List<RecyclerItem>
        get() = asyncListDiffer.currentList

    /**
     * Match view types to a given view item for easy creation of the [RecyclerView.ViewHolder].
     * Otherwise we would have to search each item in [adapterItems] and compare viewtypes
     *
     * Warning: This will only store one item even if there are multiple items for one viewtype.
     */
    private val viewTypeToItemCache = SparseArray<RecyclerItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        recyclerViewDebugLog("onCreateViewHolder(): viewType=$viewType")
        return viewTypeToItemCache[viewType].let { item ->
            val view = item.inflateView(parent)

            item.createViewHolder(view).also {
                it.provideExtras(item, this, viewHolderEventStore)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int, payloads: List<Any>) {
        recyclerViewDebugLog("onBindViewHolder() with payloads: position=$position")
        adapterItems[position].let { item ->
            item.prepareBind(holder, payloads as List<ChangePayload<DiffItem>>)
            holder.adapterItem = item
        }
    }

    /**
     * It is possible for the position to be -1 if an item is removed.
     * Otherwise, it is expected to have this called as the views go off screen
     */
    override fun onViewRecycled(holder: BaseRecyclerViewHolder) {
        recyclerViewDebugLog("onViewRecycled() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..adapterItems.size) {
            adapterItems[holder.adapterPosition].prepareUnbind(holder)
            holder.adapterItem = null
        }
    }

    override fun onFailedToRecycleView(holder: BaseRecyclerViewHolder): Boolean {
        recyclerViewDebugLog("onFailedToRecycleView() / prepareUnbind(): adapterPosition=${holder.adapterPosition}")

        if (holder.adapterPosition in 0..adapterItems.size) {
            adapterItems[holder.adapterPosition].prepareUnbind(holder)
        }

        holder.adapterItem = null

        return super.onFailedToRecycleView(holder)
    }

    override fun getItemCount() = adapterItems.size

    override fun getItemId(position: Int) = adapterItems[position].uniqueItemId

    override fun getItemViewType(position: Int) = adapterItems[position].viewType

    fun update(newItems: List<RecyclerItem>) {
        recyclerViewDebugLog("update(): newItems.size() = ${newItems.size}")

        updateAsync(newItems)

        cacheViewTypeToItem(newItems)
    }

    private fun updateAsync(newItems: List<RecyclerItem>) {
        recyclerViewDebugLog("updateAsync(): newItems.size() = ${newItems.size}")
        asyncListDiffer.submitList(newItems)
        cacheViewTypeToItem(adapterItems)
    }

    private fun cacheViewTypeToItem(items: List<RecyclerItem>) {
        recyclerViewDebugLog("cacheViewTypeToItem()")

        viewTypeToItemCache.clear()
        items.distinctBy { it.viewType }.forEach { i -> viewTypeToItemCache.append(i.viewType, i) }
    }
}