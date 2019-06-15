package com.vicidroid.amalia.sample.main

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize


sealed class MainState : ViewState {
    @Parcelize
    data class BottomNavigationItemSelected(val navigationId: Int) : MainState(), Parcelable
}

sealed class MainEvent : ViewEvent {
    class BottomNavigationChanged(val fromId: Int, val toId: Int) : MainEvent()
}