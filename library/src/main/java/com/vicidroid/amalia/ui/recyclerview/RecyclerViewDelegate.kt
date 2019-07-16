package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.ui.BaseViewDelegate

open class RecyclerViewDelegate<I : RecyclerItem<VH>, VH : BaseRecyclerViewHolder>(
    viewLifeCycleOwner: LifecycleOwner,
    rootView: View,
    @IdRes recyclerViewId: Int,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(rootView.context),
    dividerDecoration: RecyclerView.ItemDecoration = SpaceItemOffsetDecoration(rootView.context, 16)
) :
    BaseViewDelegate<RecyclerViewState<I>, ViewEvent>(
        viewLifeCycleOwner,
        rootView
    ) {

    protected val recyclerView = findViewById<RecyclerView>(recyclerViewId)
    private val adapter = DefaultRecyclerViewAdapter<I, VH>()

    init {
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerDecoration)

        adapter.setHasStableIds(true)

        recyclerView.adapter = adapter

        adapter.viewHolderEventStore.observe(viewLifeCycleOwner) { event ->
            pushEvent(event.originalEvent)
        }
    }

    override fun renderViewState(state: RecyclerViewState<I>) {
        when (state) {
            is RecyclerViewState.ListLoaded -> adapter.update(state.items)
        }
    }
}