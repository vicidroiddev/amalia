package com.vicidroid.amalia.sample.main.discover

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.ui.ListViewDelegate
import com.vicidroid.amalia.ui.ViewItem

class DiscoverViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    ListViewDelegate<DiscoverResult, DiscoverViewItem, DiscoverState, DiscoverEvent>(lifecycleOwner, view, R.id.main_discover_page_list_root) {

    override fun createViewItem(item: DiscoverResult): DiscoverViewItem {
        return DiscoverViewItem(item, layoutInflater)
    }

    override fun renderViewState(state: DiscoverState) {
        when (state) {
            is DiscoverState.Loaded -> {
                super.refreshViewItems(state.data)
            }
        }
    }
}