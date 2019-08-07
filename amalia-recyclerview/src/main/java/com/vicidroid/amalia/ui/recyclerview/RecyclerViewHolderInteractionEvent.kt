package com.vicidroid.amalia.ui.recyclerview

import com.vicidroid.amalia.core.ViewEvent

data class RecyclerViewHolderInteractionEvent(
    val adapterPosition: Int,
    val originalEvent: ViewEvent
) : ViewEvent