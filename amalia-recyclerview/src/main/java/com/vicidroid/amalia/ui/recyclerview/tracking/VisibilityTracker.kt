package com.vicidroid.amalia.ui.recyclerview.tracking

import android.graphics.Rect
import android.view.View
import com.vicidroid.amalia.ext.debugLog
import kotlin.math.roundToInt

class VisibilityTracker {
    private val rect = Rect()

    fun visibleHeightPercentage(view: View): Int {
        view.getLocalVisibleRect(rect)

        val visibleHeight = rect.height()
        val realHeight = view.measuredHeight
        val percentage = ((visibleHeight.toDouble() / realHeight.toDouble()) * 100.0).roundToInt()

//        debugLog("VisiblityTracker", "VisibleHeight: $visibleHeight | Real height: $realHeight | Percentage: $percentage")

        return percentage
    }
}