package com.vicidroid.amalia.sample

import android.app.Application
import com.vicidroid.amalia.ext.AmaliaLogging
import leakcanary.LeakSentry

class AmaliaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LeakSentry.config = LeakSentry.config.copy(watchFragmentViews = false)

        AmaliaLogging.enableFeatureLogging(AmaliaLogging.Feature.FEATURE_RECYCLER_VIEW)
    }
}