package com.vicidroid.amalia.sample.main.discover

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseRecyclerViewHolder
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

class DiscoverTvItem(val discoverResult: DiscoverResult)
    : BaseRecyclerItem<DiscoverTvItem.ViewHolder>(discoverResult) {

    override val layoutRes = R.layout.list_item_discover_tv

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun bind(viewHolder: ViewHolder, payloads: List<ChangePayload<DiffItem>>) {
        with(viewHolder) {
            name.text = discoverResult.name
            Glide.with(viewHolder.itemView.context)
                .load(discoverResult.postPathUrl)
                .into(image)
                .clearOnDetach()
        }
    }

    class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        val name: TextView = findViewById(R.id.list_item_discover_tv_name)
        val image: ImageView = findViewById(R.id.list_item_discover_tv_image)
        val removeBtn: ImageButton = findViewById(R.id.list_item_discover_tv_remove_btn)

        init {

            removeBtn.setOnClickListener {
                val item = adapterItem as DiscoverTvItem
                recyclerViewDebugLog("Clicked: ${item.discoverResult.name}")
                pushEvent(DiscoverEvent.RemoveTvShow(item.discoverResult))
            }
        }
    }
}
