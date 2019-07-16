package com.vicidroid.amalia.sample.test

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.test.TestRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewDelegate

class TestRecyclerViewDelegate(
    viewLifeCycleOwner: LifecycleOwner,
    rootView: View
) : RecyclerViewDelegate<TestRecyclerItem, TestRecyclerItem.TestViewHolder>(
    viewLifeCycleOwner,
    rootView,
    R.id.main_test_recyclerview_list_root,
//    layoutManager = GridLayoutManager(rootView.context, 2)
    layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.VERTICAL, false)
)