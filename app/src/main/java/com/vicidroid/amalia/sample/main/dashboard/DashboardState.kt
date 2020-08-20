package com.vicidroid.amalia.sample.main.dashboard

import android.os.Parcelable
import com.vicidroid.amalia.core.EphemeralState
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize

sealed class DashboardState : ViewState {
    @Parcelize
    data class Loaded(val data: String, val imageUrl: String) : DashboardState(), Parcelable
    object EphemeralStateToLoadDialog : DashboardState(), EphemeralState
}