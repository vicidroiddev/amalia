package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.os.Parcelable
import com.vicidroid.amalia.core.ViewState
import kotlinx.android.parcel.Parcelize

sealed class ExampleFragment1ViewState : ViewState, Parcelable {
    @Parcelize
    data class Loaded(val viewDiff: ExampleFragment1ViewDiff) : ExampleFragment1ViewState()

}
