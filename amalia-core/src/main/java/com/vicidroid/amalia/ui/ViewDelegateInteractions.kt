package com.vicidroid.amalia.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.res.Resources


interface ViewDelegateInteractions {
    val context: Context

    val resources: Resources
        get() = context.resources

    fun createProgressDialog() = ProgressDialog(context)
}