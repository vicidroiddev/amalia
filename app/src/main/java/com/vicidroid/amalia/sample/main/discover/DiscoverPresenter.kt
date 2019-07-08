package com.vicidroid.amalia.sample.main.discover

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.ext.debugLog
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverRepository
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DiscoverPresenter(private val repository: DiscoverRepository) :
    BasePresenter<DiscoverState, DiscoverEvent>(),
    Refreshable {

    override fun loadInitialState() {
        debugLog(TAG_INSTANCE, "loadInitialState()")
        mainScope.launch(Dispatchers.Main) {
            val results = repository.discoverFromApi()
            results.forEach { debugLog(TAG_INSTANCE, it.toString()) }
            pushState(DiscoverState.Loaded(results))
        }
    }

    override fun onRefreshRequest() {
    }

    //TODO, generalize this, could perhaps use the logic of viewModelScope.
    //https://github.com/vicidroiddev/amalia/issues/12
    private val supervisorJob = SupervisorJob()

    private val mainScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    override fun onCleared() {
        supervisorJob.cancel()
        super.onCleared()
    }
}
