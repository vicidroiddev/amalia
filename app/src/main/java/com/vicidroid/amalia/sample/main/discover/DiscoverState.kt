package com.vicidroid.amalia.sample.main.discover

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize

sealed class DiscoverState : ViewState {
    @Parcelize
    data class Loaded(val data: String, val imageUrl: String) : DiscoverState(), Parcelable
}