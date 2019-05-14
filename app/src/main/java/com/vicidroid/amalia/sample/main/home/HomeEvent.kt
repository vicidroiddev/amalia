package com.vicidroid.amalia.sample.main.home

import androidx.activity.ComponentActivity
import com.vicidroid.amalia.core.ViewEvent

sealed class HomeEvent : ViewEvent {
    object RequestSave : HomeEvent()
    class RequestNavigate(val activity: ComponentActivity) : HomeEvent()
}