package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.ui.BaseViewDelegate

open class RecyclerViewDelegate<I : RecyclerBinding<VH>, VH : BaseRecyclerViewHolder, E : ViewEvent>(
    viewLifeCycleOwner: LifecycleOwner,
    rootView: View,
    @IdRes recyclerViewId: Int,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(rootView.context)
) :
    BaseViewDelegate<RecyclerViewState<I>, E>(
        viewLifeCycleOwner,
        rootView
    ) {

    val recyclerView = findViewById<RecyclerView>(recyclerViewId)
    val adapter = DefaultRecyclerViewAdapter<I, VH>()

    init {
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun renderViewState(state: RecyclerViewState<I>) {
        when (state) {
            is RecyclerViewState.ListLoaded -> adapter.update(state.items)
        }
    }
}