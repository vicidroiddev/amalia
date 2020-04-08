package com.vicidroid.amalia.sample.main

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ext.childPresenterProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverRepository
import com.vicidroid.amalia.sample.main.dashboard.DashboardPresenter
import com.vicidroid.amalia.sample.main.discover.DiscoverPresenter
import com.vicidroid.amalia.sample.main.home.HomePresenter
import com.vicidroid.amalia.sample.test.TestPresenter
import com.vicidroid.amalia.sample.utils.toastLong
import com.vicidroid.amalia.ui.ViewDelegate

class MainPresenter : BasePresenter() {

    private val homePresenter by childPresenterProvider { HomePresenter() }
    private val dashboardPresenter by childPresenterProvider { DashboardPresenter() }
    private val discoverPresenter by childPresenterProvider { DiscoverPresenter(DiscoverRepository()) }
    private val testPresenter by childPresenterProvider { TestPresenter() }

    override fun onViewStateRestored(restoredViewState: ViewState) {
        // Just an example, we don't need to save this field since the view state is parceable, the selected tab is restored automatically :)
        applicationContext.toastLong("restored selectedBottomId: ${savedStateHandle.get<Int?>("selectedBottomId")}")
    }

    override fun loadInitialState() {
        pushNavigationItem(R.id.navigation_discover)
    }

    override fun onBindViewDelegate(viewDelegate: ViewDelegate) {
        when (viewDelegate) {
            is MainViewDelegate -> {
                homePresenter.bind(viewDelegate.homeViewDelegate)
                dashboardPresenter.bind(viewDelegate.dashboardViewDelegate)
                discoverPresenter.bind(viewDelegate.discoverViewDelegate)
                testPresenter.bind(viewDelegate.testRecyclerViewDelegate)
            }
        }
    }

    override fun onViewEvent(event: ViewEvent) {
        when (event) {
            is MainEvent.BottomNavigationChanged -> {
                when (event.toId) {
                    R.id.navigation_home -> homePresenter.onRefreshRequest()
                    R.id.navigation_dashboard -> dashboardPresenter.onRefreshRequest()
                    R.id.navigation_discover -> discoverPresenter.onRefreshRequest()
                    R.id.navigation_test_recyclerview -> {
                        testPresenter.onRefreshRequest()
                    }
                }
                pushNavigationItem(event.toId)
            }
        }
    }

    fun pushNavigationItem(itemId: Int) {
        //Example, not needed since view state is persisted automatically :)
        persist("selectedBottomId", itemId)
        pushState(MainState.BottomNavigationItemSelected(itemId))
    }
}