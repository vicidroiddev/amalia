package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.vicidroid.amalia.R
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.BaseViewDelegate
import com.vicidroid.amalia.ui.recyclerview.adapter.DefaultRecyclerViewAdapter
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem
import com.vicidroid.amalia.ui.recyclerview.diff.AsyncRecyclerItemDiffCallback

//TODO These params are becoming unwieldy, let's wrap them in a config block and set decent defaults
// • Option for vertical/horizontal linear layouts, also useful for space or divider decorators
abstract class RecyclerViewDelegate(
    viewLifeCycleOwner: LifecycleOwner,
    rootView: View,
    @IdRes recyclerViewId: Int = R.id.amalia_recycled_list,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(rootView.context),
    spaceSeparationInDp: Int = 8,
    defaultDividerDecoration: RecyclerView.ItemDecoration = SpaceItemOffsetDecoration(
        rootView.context,
        spaceSeparationInDp
    ),
    asyncDiffCallback: DiffUtil.ItemCallback<RecyclerItem> = AsyncRecyclerItemDiffCallback(),
    recyclerViewHasFixedSize: Boolean = true,
    trackItemsSeen: Boolean = false,
    visibilityThresholdPercentage: Int = 80,
    val useStickyHeaders: Boolean = false
) :
    BaseViewDelegate(
        viewLifeCycleOwner,
        rootView
    ) {

    protected val recyclerView = findViewById<RecyclerView>(recyclerViewId)
    protected val adapter = DefaultRecyclerViewAdapter(
        recyclerView.context,
        viewLifeCycleOwner,
        asyncDiffCallback,
        trackItemsSeen,
        visibilityThresholdPercentage
    )

    init {
        // ADAPTER SETUP
        adapter.setHasStableIds(true)
        adapter.viewHolderEventStore.observe(viewLifeCycleOwner) { event ->
            onInterceptEventChain(event)
            pushEvent(event.originalEvent)
        }

        // RECYCLER VIEW SETUP
        recyclerView.setHasFixedSize(recyclerViewHasFixedSize)
        recyclerView.layoutManager = layoutManager

        addItemDecorators(layoutManager, spaceSeparationInDp, defaultDividerDecoration)

        recyclerView.adapter = adapter
    }

    private fun addItemDecorators(layoutManager: RecyclerView.LayoutManager, spaceSeparationInDp: Int, dividerDecoration: RecyclerView.ItemDecoration) {
        if (useStickyHeaders) {
            recyclerViewDebugLog("Adding item decorator with sticky support")
            addStickyHeaders(layoutManager)
        } else if (spaceSeparationInDp > 0) {
            recyclerViewDebugLog("Adding item decorator with space: ${spaceSeparationInDp}dp ")
            recyclerView.addItemDecoration(dividerDecoration)
        }
    }

    open fun onInterceptEventChain(event: RecyclerViewHolderInteractionEvent) {
    }

    override fun renderViewState(state: ViewState) {
        when (state) {
            is RecyclerViewState.ListLoaded -> adapter.update(state.items)
        }
    }

    private fun addStickyHeaders(layoutManager: RecyclerView.LayoutManager) {
        val orientation = when (layoutManager.canScrollVertically()) {
            true -> DividerItemDecoration.VERTICAL
            false -> DividerItemDecoration.HORIZONTAL
        }

        val lineDecoration = DividerItemDecoration(recyclerView.context, orientation)
        val stickyHeaderDecoration = StickyRecyclerHeadersDecoration(adapter)

        recyclerView.addItemDecoration(lineDecoration)
        recyclerView.addItemDecoration(stickyHeaderDecoration)
    }
}