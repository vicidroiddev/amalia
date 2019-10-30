package com.vicidroid.amalia.sample.main.discover

import android.view.View
import android.widget.TextView
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.adapter.BaseRecyclerViewHolder
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

class DiscoverTvItemHeader(val title: String) : BaseRecyclerItem<DiscoverTvItemHeader.HeaderViewHolder>(
    object : DiffItem {
        override val diffId = title
    }) {
    override val layoutRes = R.layout.list_item_discover_tv_header

    override fun createViewHolder(itemView: View) = HeaderViewHolder(itemView)

    override fun bind(viewHolder: HeaderViewHolder, payloads: List<ChangePayload<DiffItem>>) {
        with(viewHolder) {
            titleView.text = title
        }
    }

    class HeaderViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        val titleView: TextView = findViewById(R.id.list_item_discover_tv_header_title)
    }
}
