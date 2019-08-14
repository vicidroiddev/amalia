package com.vicidroid.amalia.sample.main.discover

import androidx.lifecycle.mainScope
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.ext.debugLog
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverRepository
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoverPresenter(private val repository: DiscoverRepository) :
    BasePresenter<DiscoverState, DiscoverEvent>(),
    Refreshable {

    override fun loadInitialState() {
        debugLog(TAG_INSTANCE, "loadInitialState()")
        mainScope.launch {
            val results = repository.discoverFromApi()
            results.forEach { debugLog(TAG_INSTANCE, it.toString()) }
            pushState(DiscoverState.Loaded(results))
        }
    }

    override fun onRefreshRequest() {
    }
}
