package com.vicidroid.amalia.ext

import android.util.Log


val DEBUG = true

fun logDebug(tag: String, msg: String) {
    if (DEBUG) Log.d(tag, msg)
}