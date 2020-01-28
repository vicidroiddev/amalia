package com.vicidroid.amalia.ui.recyclerview

import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ui.recyclerview.adapter.RecyclerItem

abstract class RecyclerViewState : ViewState {
    data class ListLoaded(val items: List<RecyclerItem>) : RecyclerViewState()
}