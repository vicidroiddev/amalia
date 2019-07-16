package com.vicidroid.amalia.sample.test

import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

data class Data(
    val id: Long,
    val title: String,
    val subTitle: String,
    val hint: String,
    var editableText: String = title
) : DiffItem {
    override val diffId = id
}