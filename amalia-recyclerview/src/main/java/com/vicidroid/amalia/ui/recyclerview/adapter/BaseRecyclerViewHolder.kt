package com.vicidroid.amalia.ui.recyclerview.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewEventStore
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewHolderInteractionEvent

abstract class BaseRecyclerViewHolder(itemView: View, sticky: Boolean = false) :
    RecyclerView.ViewHolder(itemView),
    ViewHolderHelper {
    internal lateinit var eventStore: ViewEventStore<RecyclerViewHolderInteractionEvent>
    lateinit var adapter: RecyclerViewAdapter
    var adapterItem: RecyclerItem? = null

    fun provideExtras(
        adapterItem: RecyclerItem,
        adapter: RecyclerViewAdapter,
        eventStore: ViewEventStore<RecyclerViewHolderInteractionEvent>
    ) {
        this.adapter = adapter
        this.adapterItem = adapterItem
        this.eventStore = eventStore
        onExtrasProvided()
    }

    open fun onExtrasProvided() {

    }

    fun pushEvent(event: ViewEvent) {
        val wrappedEvent = RecyclerViewHolderInteractionEvent(
            adapterPosition,
            event
        )
        eventStore.pushEvent(wrappedEvent)
    }
}