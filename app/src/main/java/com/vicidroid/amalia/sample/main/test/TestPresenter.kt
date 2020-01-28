package com.vicidroid.amalia.sample.test

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.sample.main.test.TestRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewState
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem


class TestPresenter : BasePresenter<ViewState, ViewEvent>(), Refreshable {
    var data = mutableListOf<TestRecyclerItem>()

    override fun loadInitialState() {
        for (i in 1..10) {
            data.add(
                TestRecyclerItem(
                    Data(
                        i.toString(),
                        "Title $i",
                        "Subtitle $i",
                        "Default Text $i"
                    )
                )
            )
        }

        pushState(RecyclerViewState.ListLoaded(data))
    }

    override fun onRefreshRequest() {
    }
}