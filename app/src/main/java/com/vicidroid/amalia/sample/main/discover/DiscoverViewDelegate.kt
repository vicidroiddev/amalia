package com.vicidroid.amalia.sample.main.discover

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class DiscoverViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<DiscoverState, DiscoverEvent>(lifecycleOwner, view) {

    override fun renderViewState(state: DiscoverState) {
        when (state) {
            is DiscoverState.Loaded -> {
            }
        }
    }
}