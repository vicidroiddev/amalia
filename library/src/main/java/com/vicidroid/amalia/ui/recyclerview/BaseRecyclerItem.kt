package com.vicidroid.amalia.ui.recyclerview

import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

abstract class BaseRecyclerItem<VH : BaseRecyclerViewHolder>(final override val diffItem: DiffItem) : RecyclerItem<VH> {
    override val uniqueItemId = diffItem.diffId.hashCode().toLong()
}
