package com.vicidroid.amalia.sample.main

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.ext.childPresenterProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.dashboard.DashboardPresenter
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.sample.main.home.HomePresenter
import com.vicidroid.amalia.sample.main.notification.NotificationPresenter
import com.vicidroid.amalia.ui.BaseViewDelegate

class MainPresenter : BasePresenter<MainState, MainEvent>() {

    var selectedBottomId: Int = R.id.navigation_home

    val homePresenter by childPresenterProvider { HomePresenter() }
    val dashboardPresenter by childPresenterProvider { DashboardPresenter() }
    val notificationPresenter by childPresenterProvider { NotificationPresenter() }

    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<MainState, MainEvent>) {
        when (viewDelegate) {
            is MainViewDelegate -> {
                homePresenter.bind(viewDelegate.homeViewDelegate)
                dashboardPresenter.bind(viewDelegate.dashboardViewDelegate)
                notificationPresenter.bind(viewDelegate.notificationsViewDelegate)
            }
        }
        pushState(MainState.FirstLoad)
    }

    override fun onViewEvent(event: MainEvent) {
        when (event) {
            is MainEvent.BottomNavigationChanged -> {
                selectedBottomId = event.toId


                when (event.toId) {
                    R.id.navigation_home -> (homePresenter as Refreshable).onRefreshRequest()
                    R.id.navigation_dashboard -> (dashboardPresenter as Refreshable).onRefreshRequest()
                    R.id.navigation_notifications -> (notificationPresenter as Refreshable).onRefreshRequest()
                }
            }
        }
    }
}