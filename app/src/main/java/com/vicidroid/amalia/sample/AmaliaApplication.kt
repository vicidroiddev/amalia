package com.vicidroid.amalia.sample

import android.app.Application
import com.vicidroid.amalia.ext.AmaliaLogging

class AmaliaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AmaliaLogging.enableFeatureLogging(AmaliaLogging.Feature.FEATURE_RECYCLER_VIEW)
        AmaliaLogging.enableFeatureLogging(AmaliaLogging.Feature.FEATURE_PRESENTER)
    }
}