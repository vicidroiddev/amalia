package com.vicidroid.amalia.sample.main.home

import androidx.lifecycle.SavedStateHandle
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.sample.main.utils.toastLong
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomePresenter
    : BasePresenter<HomeState, HomeEvent>(),
    Refreshable {

    val imageUrl =
        "https://external-preview.redd.it/PkXSGl16_FneFtflRXaSRAVpz4N4y5vPkF3Dzr87lBs.jpg?auto=webp&s=5ab6d8ff8742928160bd424f80ad5de01df34f00"
    var timestamp: String = ""

    override fun onSaveStateHandleProvided(handle: SavedStateHandle) {
        timestamp = handle.get<String>("timestamp")?.also {
            applicationContext.toastLong("Restored from savedstate: $it")
        } ?: System.currentTimeMillis().toString()

    }

    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<HomeState, HomeEvent>) {
        loadData()
    }

    override fun onViewEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestSave -> {
                applicationContext.toastLong("Saving: $timestamp")
                savedStateHandle["timestamp"] = timestamp
            }
        }
    }

    override fun onRefreshRequest() {
        loadData(true)
    }

    fun loadData(forceReload: Boolean = false) {
        if (forceReload || timestamp.isEmpty()) timestamp = System.currentTimeMillis().toString()
        savedStateHandle["timestamp"] = timestamp

        val newState = HomeState.Loaded("Home $timestamp", imageUrl)
        pushState(newState, preferCachedState = !forceReload)
    }
}