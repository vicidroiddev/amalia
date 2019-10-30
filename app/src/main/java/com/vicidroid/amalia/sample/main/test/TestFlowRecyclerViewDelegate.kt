package com.vicidroid.amalia.sample.test

import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.sample.KeyboardStateObserver
import com.vicidroid.amalia.sample.main.test.TestRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewDelegate

class TestFlowRecyclerViewDelegate(
    activity: FragmentActivity,
    rootView: View,
    @IdRes recyclerViewId: Int
) : RecyclerViewDelegate(
    activity,
    rootView,
    recyclerViewId,
    layoutManager = LinearLayoutManager(rootView.context, RecyclerView.HORIZONTAL, false)
) {
    val snapHelper = PagerSnapHelper().apply {
        attachToRecyclerView(recyclerView)
    }

    init {
        KeyboardStateObserver(activity).keyboardStateLiveData.observe(
            activity,
            Observer<Boolean> { shown ->  Toast.makeText(context, "Keyboard shown: $shown", Toast.LENGTH_LONG).show() }
        )
    }

}