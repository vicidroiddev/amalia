package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewEventStore

abstract class BaseRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var adapterItem: RecyclerItem<*>? = null

    internal lateinit var eventStore: ViewEventStore<RecyclerViewHolderInteractionEvent>

    fun getString(@StringRes stringRes: Int) = itemView.resources.getString(stringRes)

    fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(itemView.context, colorRes)

    fun <T : View> findViewById(@IdRes id: Int): T = itemView.findViewById(id)

    fun pushEvent(event: ViewEvent) {
        val wrappedEvent = RecyclerViewHolderInteractionEvent(adapterPosition, event)
        eventStore.pushEvent(wrappedEvent)
    }
}

