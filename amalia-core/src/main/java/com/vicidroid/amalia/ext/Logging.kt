package com.vicidroid.amalia.ext

import android.util.Log

const val DEBUG_LOGGING = true
const val RECYCLER_VIEW_LOGGING = true
const val RECYCLER_VIEW = "AmaliaRecyclerView"

fun debugLog(tag: String, msg: String) {
    if (DEBUG_LOGGING) Log.v(tag, msg)
}


fun recyclerViewDebugLog(msg: String) {
    if (RECYCLER_VIEW_LOGGING) Log.v(RECYCLER_VIEW, msg)
}