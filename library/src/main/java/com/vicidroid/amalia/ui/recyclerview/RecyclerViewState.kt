package com.vicidroid.amalia.ui.recyclerview

import com.vicidroid.amalia.core.ViewState

abstract class RecyclerViewState<T> : ViewState {
    data class ListLoaded<T>(val items: List<T>) : RecyclerViewState<T>()
}