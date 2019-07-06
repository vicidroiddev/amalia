package com.vicidroid.amalia.sample.main.notification

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize

sealed class NavigationState : ViewState {
    @Parcelize
    data class Loaded(val data: String, val imageUrl: String) : NavigationState(), Parcelable
}