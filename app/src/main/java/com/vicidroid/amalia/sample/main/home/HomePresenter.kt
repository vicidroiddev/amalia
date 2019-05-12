package com.vicidroid.amalia.sample.main.home

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomePresenter
    : BasePresenter<HomeState, HomeEvent>(),
    Refreshable {

    val imageUrl = "https://external-preview.redd.it/PkXSGl16_FneFtflRXaSRAVpz4N4y5vPkF3Dzr87lBs.jpg?auto=webp&s=5ab6d8ff8742928160bd424f80ad5de01df34f00"

    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<HomeState, HomeEvent>) {
        calculateTimestamp()
    }

    override fun onRefreshRequest() {
        calculateTimestamp(true)
    }

    fun calculateTimestamp(force: Boolean = false) {
        val newState = HomeState.Loaded("Home " + System.currentTimeMillis().toString(), imageUrl)
        pushState(newState, preferCachedState = !force)
    }
}