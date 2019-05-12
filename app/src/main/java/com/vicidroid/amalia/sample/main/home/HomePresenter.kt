package com.vicidroid.amalia.sample.main.home

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomePresenter
    : BasePresenter<HomeState, HomeEvent>(),
    Refreshable {

    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<HomeState, HomeEvent>) {
        calculateTimestamp()
    }

    override fun onRefreshRequest() {
        calculateTimestamp(true)
    }

    fun calculateTimestamp(force: Boolean = false) {
        val newState = HomeState.Loaded("Home " + System.currentTimeMillis().toString())
        pushState(newState, preferCachedState = !force)
    }
}