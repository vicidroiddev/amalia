package com.vicidroid.amalia.ui.recyclerview.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.vicidroid.amalia.core.ViewEventStore
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.recyclerview.AmaliaCommonEvent
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewHolderInteractionEvent
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem
import com.vicidroid.amalia.ui.recyclerview.tracking.VisibilityTracker

open class DefaultRecyclerViewAdapter(
    final override val context: Context,
    final override val lifecycleOwner: LifecycleOwner,
    private val asyncDiffCallback: DiffUtil.ItemCallback<RecyclerItem>,
    private val trackItemsSeen: Boolean,
    private val visibilityThresholdPercentage: Int
) :
    RecyclerView.Adapter<BaseRecyclerViewHolder>(),
    RecyclerViewAdapter,
    StickyRecyclerHeadersAdapter<BaseHeaderViewHolder> {

    internal val viewHolderEventStore = ViewEventStore<RecyclerViewHolderInteractionEvent>()

    private val layoutInflater = LayoutInflater.from(context)!!

    val adapterItems: List<RecyclerItem>
        get() = asyncListDiffer.currentList

    private val asyncListDiffer = AsyncListDiffer(this, asyncDiffCallback)

    /**
     * Match view types to a given view item for easy creation of the [RecyclerView.ViewHolder].
     * Otherwise we would have to search each item in [adapterItems] and compare viewtypes
     *
     * Warning: This will only store one item even if there are multiple items for one viewtype.
     */
    private val viewTypeToItemCache = SparseArray<RecyclerItem>()

    /**
     * Caches DiffItems that have been seen according to [RecyclerView.OnScrollListener]
     */
    private val itemsSeenRecently = mutableMapOf<String, DiffItem>()

    private val itemSeenScrollListener = object : RecyclerView.OnScrollListener() {
        /**
         * We need to track the initial items displayed even if the scroll state has not yet
         * changed to [RecyclerView.SCROLL_STATE_IDLE]
         */
        var scrollStateHasIdledAtLeastOnce = false

        val visibilityTracker = VisibilityTracker()


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!scrollStateHasIdledAtLeastOnce) {
                trackNewItemsSeen(recyclerView)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    scrollStateHasIdledAtLeastOnce = true
                    trackNewItemsSeen(recyclerView)
                }

                RecyclerView.SCROLL_STATE_IDLE -> {
                    scrollStateHasIdledAtLeastOnce = true
                    trackNewItemsSeen(recyclerView)
                }

                RecyclerView.SCROLL_STATE_SETTLING -> {
                }
            }
        }

        private fun trackNewItemsSeen(recyclerView: RecyclerView) {
            val layoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager
            if (layoutManager !is LinearLayoutManager) return

            val itemPositionStart = layoutManager.findFirstVisibleItemPosition()
            val itemPositionEnd = layoutManager.findLastVisibleItemPosition()

            if (itemPositionStart == -1 || itemPositionEnd == -1) return

            val newItemsSeen = mutableSetOf<DiffItem>()

            for (i in itemPositionStart..itemPositionEnd) {
                trackViewWithinThreshold(i, layoutManager.findViewByPosition(i), newItemsSeen)
            }

            newItemsSeen.forEach { itemsSeenRecently[it.diffId] = it }

            //TODO buffer queue
            if (newItemsSeen.isNotEmpty()) {
                viewHolderEventStore.pushEvent(
                    RecyclerViewHolderInteractionEvent(
                        -1,
                        AmaliaCommonEvent.NewVisibleItemsDetected(newItemsSeen)
                    )
                )
            }
        }

        private fun trackViewWithinThreshold(position: Int, candidate: View?, newItemsSeen: MutableSet<DiffItem>) {
            if (candidate == null) return
            if (visibilityTracker.visibleHeightPercentage(candidate) < visibilityThresholdPercentage) return

            val item = adapterItems[position].diffItem

            if (item.diffId !in itemsSeenRecently.keys) {
//                recyclerViewDebugLog("Visible item ${adapterItems[position].javaClass} / ${item.diffId} at position $position")
                newItemsSeen.add(item)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (trackItemsSeen) {
            recyclerView.addOnScrollListener(itemSeenScrollListener)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (trackItemsSeen) {
            recyclerView.removeOnScrollListener(itemSeenScrollListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        recyclerViewDebugLog("onCreateViewHolder(): viewType=$viewType")
        return viewTypeToItemCache[viewType].let { item ->
            val view = layoutInflater.inflate(item.layoutRes, parent, false)

            item.createViewHolder(view).also {
                it.provideExtras(item, this, viewHolderEventStore)
            }
        }
    }

    /**
     * For sticky header support
     */
    override fun onCreateHeaderViewHolder(parent: ViewGroup, position: Int): BaseHeaderViewHolder {
        recyclerViewDebugLog("onCreateHeaderViewHolder(): position=$position")

        return adapterItems[position].let { item ->
            if (item.headerLayoutRes == 0) error("Expected to have a valid headerLayoutRes.")

            val view = layoutInflater.inflate(item.headerLayoutRes, parent, false)

            item.createHeaderViewHolder(view)
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
     * For sticky header support
     * Will direct the bind call to the view item
     */
    override fun onBindHeaderViewHolder(viewHolder: BaseHeaderViewHolder, position: Int) {
        adapterItems[position].prepareBindHeader(viewHolder)
    }

    /**
     * It is possible for [holder.adapterPosition] to be -1 if an item is removed.
     * It is also possible for [holder.adapterPosition] to be larger than the max index of the current adapter items.
     * Otherwise, it is expected to have this called as the views go off screen
     */
    override fun onViewRecycled(holder: BaseRecyclerViewHolder) {
        recyclerViewDebugLog("onViewRecycled() / unbind(): adapterPosition=${holder.adapterPosition}")

        if (validRange(holder.adapterPosition)) {
            adapterItems[holder.adapterPosition].prepareUnbind(holder)
            holder.adapterItem = null
        }
    }

    override fun onFailedToRecycleView(holder: BaseRecyclerViewHolder): Boolean {
        recyclerViewDebugLog("onFailedToRecycleView() / prepareUnbind(): adapterPosition=${holder.adapterPosition}")

        if (validRange(holder.adapterPosition)) {
            adapterItems[holder.adapterPosition].prepareUnbind(holder)
        }

        holder.adapterItem = null

        return super.onFailedToRecycleView(holder)
    }

    override fun getItemCount() = adapterItems.size

    override fun getItemId(position: Int) = adapterItems[position].uniqueItemId

    /**
     * Provide the header id for this item if it belongs to a section.
     * Otherwise return 0 for no section
     */
    override fun getHeaderId(position: Int): Long {
        val id = adapterItems.getOrNull(position)?.headerId ?: 0
        if (id < 0) error("sectionId must be positive. It was: $id.")
        return id
    }

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

    private fun validRange(position: Int) =
        position >= 0 && position < adapterItems.size
}