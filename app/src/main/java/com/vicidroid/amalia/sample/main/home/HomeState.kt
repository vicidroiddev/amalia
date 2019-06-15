package com.vicidroid.amalia.sample.main.home

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize

sealed class HomeState : ViewState, Parcelable {
    @Parcelize
    data class Loaded(val data: String, val imageUrl: String) : HomeState()
}
