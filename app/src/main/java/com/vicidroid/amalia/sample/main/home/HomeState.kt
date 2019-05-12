package com.vicidroid.amalia.sample.main.home

import com.vicidroid.amalia.core.ViewState

sealed class HomeState : ViewState {
    class Loaded(val data: String) : HomeState()
}
