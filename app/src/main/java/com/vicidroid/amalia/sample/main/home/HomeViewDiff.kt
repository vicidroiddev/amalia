package com.vicidroid.amalia.sample.main.home

import com.vicidroid.amalia.core.viewdiff.BaseViewDiff
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeViewDiff(
    var firstName: String = "",
    var lastName: String = "",
    var middleName: String = "",
    var hasMiddleName: Boolean = false,
    var nickName: String = ""
) : BaseViewDiff()