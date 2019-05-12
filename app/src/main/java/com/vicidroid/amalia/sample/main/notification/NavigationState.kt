package com.vicidroid.amalia.sample.main.notification

import com.vicidroid.amalia.core.ViewState

sealed class NavigationState : ViewState {
    class Loaded(val data: String) : NavigationState()
}
