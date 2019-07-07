package com.vicidroid.amalia.sample.main.discover

import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult

sealed class DiscoverState : ViewState {
    data class Loaded(val data: List<DiscoverResult>) : DiscoverState()
}