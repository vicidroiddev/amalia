package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.R
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.ui.BaseViewDelegate
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseRecyclerViewHolder
import com.vicidroid.amalia.ui.recyclerview.adapter.DefaultRecyclerViewAdapter
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem
import com.vicidroid.amalia.ui.recyclerview.diff.AsyncRecyclerItemDiffCallback

open class RecyclerViewDelegate<I : RecyclerItem<VH>, VH : BaseRecyclerViewHolder>(
    viewLifeCycleOwner: LifecycleOwner,
    rootView: View,
    @IdRes recyclerViewId: Int = R.id.amalia_recycled_list,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(rootView.context),
    spaceSeparationInDp: Int = 8,
    dividerDecoration: RecyclerView.ItemDecoration = SpaceItemOffsetDecoration(
        rootView.context,
        spaceSeparationInDp
    ),
    asyncDiffCallback: DiffUtil.ItemCallback<I> = AsyncRecyclerItemDiffCallback()
) :
    BaseViewDelegate<RecyclerViewState<I>, ViewEvent>(
        viewLifeCycleOwner,
        rootView
    ) {

    protected val recyclerView = findViewById<RecyclerView>(recyclerViewId)
    protected val adapter = DefaultRecyclerViewAdapter(
        viewLifeCycleOwner,
        this,
        asyncDiffCallback
    )

    init {
        // ADAPTER SETUP
        adapter.setHasStableIds(true)
        adapter.viewHolderEventStore.observe(viewLifeCycleOwner) { event ->
            onInterceptEventChain(event)
            pushEvent(event.originalEvent)
        }

        // RECYCLER VIEW SETUP
        recyclerView.layoutManager = layoutManager

        if (spaceSeparationInDp > 0) {
            recyclerViewDebugLog("Adding item decorator with space: ${spaceSeparationInDp}dp ")
            recyclerView.addItemDecoration(dividerDecoration)
        }

        recyclerView.adapter = adapter
    }


    open fun onInterceptEventChain(event: RecyclerViewHolderInteractionEvent) {
    }

    override fun renderViewState(state: RecyclerViewState<I>) {
        when (state) {
            is RecyclerViewState.ListLoaded -> adapter.update(state.items)
        }
    }
}