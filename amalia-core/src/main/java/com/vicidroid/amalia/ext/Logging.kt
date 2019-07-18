package com.vicidroid.amalia.ext

import android.util.Log
import com.vicidroid.amalia.ext.AmaliaLogging.Feature.Companion.FEATURE_RECYCLER_VIEW

const val DEBUG_LOGGING = true

fun debugLog(tag: String, msg: String) {
    if (DEBUG_LOGGING) Log.v(tag, msg)
}


fun recyclerViewDebugLog(msg: String) {
    if (FEATURE_RECYCLER_VIEW.enabled) Log.v(FEATURE_RECYCLER_VIEW, msg)
}

@AmaliaLogging.Feature
val @receiver:AmaliaLogging.Feature String.enabled
    get() = AmaliaLogging.features.contains(this)

object AmaliaLogging {

    val features: MutableSet<String> = mutableSetOf()

    fun enableFeatureLogging(@Feature feature: String) = features.add(feature)

    annotation class Feature {
        companion object {
            @Feature
            const val FEATURE_RECYCLER_VIEW = "AmaliaRecyclerView"
        }
    }
}