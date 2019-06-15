package com.vicidroid.amalia.sample.main

import androidx.lifecycle.SavedStateHandle
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.ext.childPresenterProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.dashboard.DashboardPresenter
import com.vicidroid.amalia.sample.main.home.HomePresenter
import com.vicidroid.amalia.sample.main.notification.NotificationPresenter
import com.vicidroid.amalia.ui.ViewDelegate

class MainPresenter : BasePresenter<MainState, MainEvent>() {

    private var selectedBottomId: Int = R.id.navigation_home

    private val homePresenter by childPresenterProvider { HomePresenter() }
    private val dashboardPresenter by childPresenterProvider { DashboardPresenter() }
    private val notificationPresenter by childPresenterProvider { NotificationPresenter() }

    override fun onRestoreFromProcessDeath(handle: SavedStateHandle, restoredViewState: Boolean) {
        handle.get<Int?>("selectedBottomId")?.let {
            selectedBottomId = it
        }
    }

    override fun onBindViewDelegate(viewDelegate: ViewDelegate<MainState, MainEvent>, restoredViewState: Boolean) {
        when (viewDelegate) {
            is MainViewDelegate -> {
                homePresenter.bind(viewDelegate.homeViewDelegate)
                dashboardPresenter.bind(viewDelegate.dashboardViewDelegate)
                notificationPresenter.bind(viewDelegate.notificationsViewDelegate)
            }
        }
        if (!restoredViewState) {
            pushNavigationItem(R.id.navigation_home)
        }
    }

    override fun onViewEvent(event: MainEvent) {
        when (event) {
            is MainEvent.BottomNavigationChanged -> {
                when (event.toId) {
                    R.id.navigation_home -> homePresenter.onRefreshRequest()
                    R.id.navigation_dashboard -> dashboardPresenter.onRefreshRequest()
                    R.id.navigation_notifications -> notificationPresenter.onRefreshRequest()
                }
                pushNavigationItem(event.toId)
            }
        }
    }

    fun pushNavigationItem(itemId: Int) {
        selectedBottomId = itemId
        persist("selectedBottomId", selectedBottomId)
        pushState(MainState.BottomNavigationItemSelected(selectedBottomId))
    }
}