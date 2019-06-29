package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import com.vicidroid.amalia.core.viewdiff.BaseViewDiff
import kotlinx.android.parcel.Parcelize

@Parcelize
class ExampleFragment1ViewDiff(
    var name: String = ""
) : BaseViewDiff()