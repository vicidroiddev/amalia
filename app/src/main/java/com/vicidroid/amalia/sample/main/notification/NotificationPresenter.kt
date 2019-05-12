package com.vicidroid.amalia.sample.main.notification

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.BaseViewDelegate

class NotificationPresenter : BasePresenter<NavigationState, NavigationEvent>(),
    Refreshable {
    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<NavigationState, NavigationEvent>) {
        calculateTime()
    }

    override fun onRefreshRequest() {
        calculateTime()
    }

    fun calculateTime() {
        pushState(NavigationState.Loaded("Notification: " + System.currentTimeMillis().toString()))
    }
}