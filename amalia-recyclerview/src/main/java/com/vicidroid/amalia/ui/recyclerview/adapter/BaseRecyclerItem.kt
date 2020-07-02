package com.vicidroid.amalia.ui.recyclerview.adapter

import android.view.View
import com.vicidroid.amalia.ui.recyclerview.diff.ChangePayload
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

abstract class BaseRecyclerItem<VH : BaseRecyclerViewHolder>(final override val diffItem: DiffItem) :
    RecyclerItem {

    override val uniqueItemId = diffItem.diffId.hashCode().toLong()

    /**
     * See [RecyclerItem.prepareBind]
     */
    abstract fun bind(viewHolder: VH, payloads: List<ChangePayload<DiffItem>>)

    /**
     * See [RecyclerItem.prepareBindHeader]
     * Optional, only needed when sticky headers requested.
     */
    open fun bindHeader(viewHolder: BaseHeaderViewHolder) {}

    /**
     * See [RecyclerItem.prepareUnbind]
     */
    open fun unbind(viewHolder: VH) {}

    @Suppress("UNCHECKED_CAST")
    final override fun prepareBind(viewHolder: BaseRecyclerViewHolder, payloads: List<ChangePayload<DiffItem>>) {
        bind(viewHolder as VH, payloads)
    }

    @Suppress("UNCHECKED_CAST")
    final override fun prepareUnbind(viewHolder: BaseRecyclerViewHolder) {
        unbind(viewHolder as VH)
    }

    @Suppress("UNCHECKED_CAST")
    final override fun prepareBindHeader(viewHolder: BaseHeaderViewHolder) {
        bindHeader(viewHolder)
    }

    override fun createHeaderViewHolder(itemView: View): BaseHeaderViewHolder {
        error(
            "createHeaderViewHolder() must be overridden if sticky headers are desired. " +
                    "This method will not be called if sticky headers are disabled."
        )
    }
}
