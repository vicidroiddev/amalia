package com.vicidroid.amalia.sample.main.dashboard

import com.vicidroid.amalia.core.ViewState

sealed class DashboardState : ViewState {
    class Loaded(val data: String, val imageUrl: String) : DashboardState()
}