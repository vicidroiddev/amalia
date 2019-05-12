package com.vicidroid.amalia.sample.main.dashboard

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.ui.BaseViewDelegate

class DashboardPresenter : BasePresenter<DashboardState, DashboardEvent>(),
    Refreshable {
    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<DashboardState, DashboardEvent>) {
        calculateTimestamp()
    }

    override fun onRefreshRequest() {
        calculateTimestamp(true)
    }

    fun calculateTimestamp(force: Boolean = false) {
        pushState(
            DashboardState.Loaded("Dashboard " + System.currentTimeMillis().toString()),
            preferCachedState = !force
        )
    }
}