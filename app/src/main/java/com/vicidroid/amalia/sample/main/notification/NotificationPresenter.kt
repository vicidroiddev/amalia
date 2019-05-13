package com.vicidroid.amalia.sample.main.notification

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.BaseViewDelegate

class NotificationPresenter : BasePresenter<NavigationState, NavigationEvent>(),
    Refreshable {

    val imageUrl = "https://i.redd.it/owhsn5k98hx21.jpg"
    
    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<NavigationState, NavigationEvent>) {
        calculateTimestamp()
    }

    override fun onRefreshRequest() {
        calculateTimestamp(true)
    }

    fun calculateTimestamp(force: Boolean = false) {
        pushState(
            NavigationState.Loaded("Notification: " + System.currentTimeMillis().toString(), imageUrl),
            preferCachedState = !force)
    }
}