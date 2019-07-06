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
import com.vicidroid.amalia.sample.main.discover.DiscoverViewDelegate
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
        true
    }

    val homeViewDelegate by viewDelegateProvider {
        HomeViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_home_page
            )
        )
    }

    val dashboardViewDelegate by viewDelegateProvider {
        DashboardViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_dashboard_page
            )
        )
    }

    val discoverViewDelegate by viewDelegateProvider {
        DiscoverViewDelegate(
            viewLifeCycleOwner,
            context.inflate(
                R.layout.main_discover_page
            )
        )
    }

    val navigationIdMap = SparseArray<BaseViewDelegate<*, *>>(3).apply {
        append(R.id.navigation_home, homeViewDelegate)
        append(R.id.navigation_dashboard, dashboardViewDelegate)
        append(R.id.navigation_discover, discoverViewDelegate)
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