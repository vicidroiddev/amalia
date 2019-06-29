package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.util.Log
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.viewdiff.ViewDiff

class ExampleFragment1Presenter : BasePresenter<ExampleFragment1ViewState, ExampleFragment1ViewEvent>() {

    override fun loadInitialState() {
        Log.v(TAG_INSTANCE, "loadInitialState()")
        pushState(ExampleFragment1ViewState.Loaded(ExampleFragment1ViewDiff()))
    }

    override fun onViewDiffReceived(viewDiff: ViewDiff) {
        updateViewStateSilently { old ->
            ExampleFragment1ViewState.Loaded(viewDiff as ExampleFragment1ViewDiff)
        }
    }
}
