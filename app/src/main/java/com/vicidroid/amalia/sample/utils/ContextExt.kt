package com.vicidroid.amalia.sample.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

fun Context.inflate(layout: Int, root: View? = null, attach: Boolean = false) =
    LayoutInflater.from(this).inflate(layout, root as ViewGroup?, attach)

fun Context.toastShort(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).also { it.show() }

fun Context.toastLong(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_LONG).also { it.show() }
