package com.vicidroid.amalia.sample.main.discover

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vicidroid.amalia.ext.recyclerViewDebugLog
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.ui.recyclerview.BaseRecyclerViewHolder
import com.vicidroid.amalia.ui.recyclerview.RecyclerItem

class DiscoverTvItem(val discoverResult: DiscoverResult) : RecyclerItem<DiscoverTvItem.ViewHolder> {
    override val layoutRes = R.layout.list_item_discover_tv
    override val uniqueItemId = discoverResult.id.toLong()
    override val diffItem = discoverResult

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun bind(viewHolder: ViewHolder) {
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
