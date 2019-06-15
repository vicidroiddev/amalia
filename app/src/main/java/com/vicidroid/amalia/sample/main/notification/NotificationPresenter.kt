package com.vicidroid.amalia.sample.main.notification

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.ViewDelegate

class NotificationPresenter : BasePresenter<NavigationState, NavigationEvent>(),
    Refreshable {

    val imageUrl = "https://i.redd.it/owhsn5k98hx21.jpg"

    override fun onBindViewDelegate(viewDelegate: ViewDelegate<NavigationState, NavigationEvent>, restoredViewState: Boolean) {
        if (!restoredViewState) {
            calculateTimestamp()
        }
    }

    override fun onRefreshRequest() {
        calculateTimestamp()
    }

    fun calculateTimestamp() {
        pushState(
            NavigationState.Loaded("Notification: " + System.currentTimeMillis().toString(), imageUrl)
        )
    }
}