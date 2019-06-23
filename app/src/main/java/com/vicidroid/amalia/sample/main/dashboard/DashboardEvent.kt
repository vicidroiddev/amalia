package com.vicidroid.amalia.sample.main.dashboard

import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.core.ViewEvent

sealed class DashboardEvent : ViewEvent {
    class OpenFragmentExample(val hostActivity: AppCompatActivity) : DashboardEvent()
}