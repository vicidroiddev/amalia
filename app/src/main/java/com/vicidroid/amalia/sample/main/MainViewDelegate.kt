package com.vicidroid.amalia.sample.main

import android.transition.TransitionManager
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vicidroid.amalia.ext.viewDelegateProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.dashboard.DashboardViewDelegate
import com.vicidroid.amalia.sample.main.home.HomeViewDelegate
import com.vicidroid.amalia.sample.main.notification.NavigationViewDelegate
import com.vicidroid.amalia.sample.utils.inflate
import com.vicidroid.amalia.ui.BaseViewDelegate

class MainViewDelegate(viewLifeCycleOwner: LifecycleOwner, rootView: View) :
    BaseViewDelegate<MainState, MainEvent>(
        viewLifeCycleOwner,
        rootView
    ) {

    private val anchor: FrameLayout = findViewById(R.id.mainAnchor)
    private val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        pushEvent(MainEvent.BottomNavigationChanged(bottomNavigationView.selectedItemId, item.itemId))

        when (item.itemId) {
            R.id.navigation_home -> {
                homeViewDelegate.showDelegate()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                dashboardViewDelegate.showDelegate()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                notificationsViewDelegate.showDelegate()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    val homeViewDelegate by viewDelegateProvider {
        HomeViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_home_page,
                anchor
            )
        )
    }

    val dashboardViewDelegate by lazy {
        DashboardViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_dashboard_page,
                anchor
            )
        )
    }

    val notificationsViewDelegate by lazy {
        NavigationViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_notifications_page,
                anchor
            )
        )
    }

    val navigationIdMap = SparseArray<BaseViewDelegate<*, *>>(3).apply {
        append(R.id.navigation_home, homeViewDelegate)
        append(R.id.navigation_dashboard, dashboardViewDelegate)
        append(R.id.navigation_notifications, notificationsViewDelegate)
    }

    init {
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }


    override fun renderViewState(state: MainState) {
        when (state) {
            is MainState.BottomNavigationItemSelected -> renderBottomNavigationItem(state.navigationId)
        }
    }

    private fun renderBottomNavigationItem(navigationId: Int) {
        navigationIdMap[navigationId].showDelegate()
    }

    private fun BaseViewDelegate<*, *>.showDelegate() {
        TransitionManager.beginDelayedTransition(anchor)
        anchor.removeAllViews()
        anchor.addView(this.rootView)
    }
}