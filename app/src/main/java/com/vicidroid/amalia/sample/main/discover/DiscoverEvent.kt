package com.vicidroid.amalia.sample.main.discover

import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.ui.ClickViewEvent

sealed class DiscoverEvent : ViewEvent {
    class RemoveTvShow(val discoverResult: DiscoverResult) : DiscoverEvent(), ClickViewEvent
}