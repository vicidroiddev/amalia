package com.vicidroid.amalia.sample.main.discover

import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult

sealed class DiscoverEvent : ViewEvent {
    class RemoveTvShow(val discoverResult: DiscoverResult) : DiscoverEvent()
}