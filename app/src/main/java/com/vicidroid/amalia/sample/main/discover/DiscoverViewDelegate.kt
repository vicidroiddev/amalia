package com.vicidroid.amalia.sample.main.discover

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewDelegate

class DiscoverViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    RecyclerViewDelegate(
        lifecycleOwner,
        view,
        R.id.main_discover_page_list_root,
        trackItemsSeen = true) {
}