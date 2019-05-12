package com.vicidroid.amalia.sample.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.dashboard.DashboardViewDelegate
import com.vicidroid.amalia.sample.main.home.HomeViewDelegate
import com.vicidroid.amalia.sample.main.notification.NavigationViewDelegate
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

    init {
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    val homeViewDelegate = HomeViewDelegate(
        viewLifeCycleOwner,
        context.inflate(
            R.layout.main_home_page,
            anchor
        )
    )

    val dashboardViewDelegate = DashboardViewDelegate(
        viewLifeCycleOwner,
        context.inflate(
            R.layout.main_dashboard_page,
            anchor
        )
    )

    val notificationsViewDelegate = NavigationViewDelegate(
        viewLifeCycleOwner,
        context.inflate(
            R.layout.main_notifications_page,
            anchor
        )
    )


    override fun renderViewState(state: MainState) {
        when (state) {
            MainState.FirstLoad -> renderFirstLoad()
        }
    }

    private fun renderFirstLoad() {
        homeViewDelegate.showDelegate()
    }

    private fun BaseViewDelegate<*, *>.showDelegate() {
        anchor.removeAllViews()
        anchor.addView(this.rootView)
    }

    private fun Context.inflate(layout: Int, root: View? = null, attach: Boolean = false) =
        LayoutInflater.from(this).inflate(layout, root as ViewGroup?, attach)

}