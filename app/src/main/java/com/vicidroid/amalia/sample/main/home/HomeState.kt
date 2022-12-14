package com.vicidroid.amalia.sample.main.home

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewState
import kotlinx.parcelize.Parcelize

sealed class HomeState : ViewState, Parcelable {
    @Parcelize
    data class Loaded(
        val data: String,
        val imageUrl: String,
        val firstName: String = "",
        val lastName: String = "",
        val middleName: String = "",
        val hasMiddleName: Boolean = false,
        val nickName: String = ""
    ) : HomeState()
}
