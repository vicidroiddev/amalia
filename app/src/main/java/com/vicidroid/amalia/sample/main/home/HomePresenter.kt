package com.vicidroid.amalia.sample.main.home

import android.content.Intent
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.main.MainActivity
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.core.viewdiff.ViewDiff

class HomePresenter
    : BasePresenter<HomeState, HomeEvent>(),
    Refreshable {

    val imageUrl =
        "https://external-preview.redd.it/PkXSGl16_FneFtflRXaSRAVpz4N4y5vPkF3Dzr87lBs.jpg?auto=webp&s=5ab6d8ff8742928160bd424f80ad5de01df34f00"

    override fun onViewDiffReceived(viewDiff: ViewDiff) {
        (viewDiff as? HomeViewDiff)?.let { homeViewDiff ->
                updateViewStateSilently { oldState ->
                    when (oldState) {
                        is HomeState.Loaded -> oldState.copy(
                            firstName = homeViewDiff.firstName,
                            lastName = homeViewDiff.lastName,
                            middleName = homeViewDiff.middleName,
                            hasMiddleName = homeViewDiff.hasMiddleName)
                    }
                }
        }
    }

    override fun loadInitialState() {
        reloadData()
    }

    override fun onViewEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestNavigate -> {
                //Yep, just open the same activity, eventually we can try to get an OOM exception with very large bitmaps rendered
                event.activity.run { startActivity(Intent(this, MainActivity::class.java)) }
            }
        }
    }

    override fun onRefreshRequest() {
        reloadData()
    }

    fun reloadData() {
        val timestamp = System.currentTimeMillis().toString()
        pushState(HomeState.Loaded("Home $timestamp", imageUrl, firstName = "Vici", nickName = "Vicidroid"))
    }
}