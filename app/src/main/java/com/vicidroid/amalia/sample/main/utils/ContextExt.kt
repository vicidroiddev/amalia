package com.vicidroid.amalia.sample.main.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun Context.inflate(layout: Int, root: View? = null, attach: Boolean = false) =
        LayoutInflater.from(this).inflate(layout, root as ViewGroup?, attach)