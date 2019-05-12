package com.vicidroid.amalia.sample.main

import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState


sealed class MainState : ViewState {
    object FirstLoad : MainState()

}

sealed class MainEvent : ViewEvent {
    class BottomNavigationChanged(val fromId: Int, val toId: Int) : MainEvent()
}