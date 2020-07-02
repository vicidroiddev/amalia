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
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseHeaderViewHolder
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem
import kotlin.math.abs

class DiscoverTvItem(val discoverResult: DiscoverResult)
    : BaseRecyclerItem<DiscoverTvItem.ViewHolder>(discoverResult) {

    override val layoutRes = R.layout.list_item_discover_tv_not_rounded
//    override val layoutRes = R.layout.list_item_discover_tv
//    override val headerLayoutRes = R.layout.list_item_discover_tv_header
    override val headerLayoutRes = R.layout.list_item_discover_tv_header_not_rounded
    override val headerId: Long = abs(discoverResult.firstAirDate.hashCode().toLong())

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun createHeaderViewHolder(itemView: View) = MyHeaderViewHolder(itemView)

    override fun bind(viewHolder: ViewHolder, payloads: List<ChangePayload<DiffItem>>) {
        with(viewHolder) {
            name.text = discoverResult.name
            date.text = discoverResult.firstAirDate

            Glide.with(viewHolder.itemView.context)
                .load(discoverResult.postPathUrl)
                .into(image)
                .clearOnDetach()
        }
    }

    override fun bindHeader(viewHolder: BaseHeaderViewHolder) {
        (viewHolder as MyHeaderViewHolder).title.text = discoverResult.firstAirDate
    }

    class ViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        val name: TextView = findViewById(R.id.list_item_discover_tv_name)
        val date: TextView = findViewById(R.id.list_item_discover_date)
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

    class MyHeaderViewHolder(itemView: View) : BaseHeaderViewHolder(itemView) {
        val title = findViewById<TextView>(R.id.list_item_discover_tv_header_title)
    }
}
