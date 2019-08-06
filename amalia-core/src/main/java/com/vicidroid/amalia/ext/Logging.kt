package com.vicidroid.amalia.ext

import android.util.Log

const val DEBUG_LOGGING = true

//TODO add logging hook here, perhaps include timber

fun debugLog(tag: String, msg: String) {
    if (DEBUG_LOGGING) Log.v(tag, msg)
}