package com.vicidroid.amalia.sample.main.discover

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.ui.ViewItem

class DiscoverViewItem(
    item: DiscoverResult,
    inflater: LayoutInflater
) : ViewItem(
    inflater.inflate(R.layout.list_item_discover_tv, null, false)
) {
    val name: TextView = findViewById(R.id.list_item_discover_tv_name)
    val image: ImageView = findViewById(R.id.list_item_discover_tv_image)

    init {
        name.text = item.name
        Glide.with(context).load(item.postPathUrl).into(image).clearOnDetach()
    }
}
