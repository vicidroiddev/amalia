package com.vicidroid.amalia.sample

import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class KeyboardStateObserver(
    activity: FragmentActivity,
    viewLifecycleOwner: LifecycleOwner = activity
) : DefaultLifecycleObserver {

    /**
     * Observe on this live data to receive changes on the keyboard shown status
     */
    val keyboardStateLiveData = MutableLiveData<Boolean>()

    var keyboardShown = false

    /**
     * Assume:
     * • each key is a minimum of 32dp (Material Design touch guidelines)
     * • there are at least 4 rows (three rows for letters, one for space bar)
     */
    private val heightDiffThreshold =
        (32 * 4) * activity.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

    private val decorView = activity.window.decorView
    private var visibleScreenRect = Rect()
    private var visibleScreenHeight = 0
    private var fullScreenHeight = 0
    private var suspectedKeyboardSize = 0

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        decorView.getWindowVisibleDisplayFrame(visibleScreenRect)
        visibleScreenHeight = visibleScreenRect.bottom
        fullScreenHeight = decorView.context.resources.displayMetrics.heightPixels

        suspectedKeyboardSize = fullScreenHeight - visibleScreenHeight

        keyboardShown = (suspectedKeyboardSize >= heightDiffThreshold)

        if (keyboardStateLiveData.value != keyboardShown) {
            keyboardStateLiveData.value = keyboardShown
        }
    }

    init {
        viewLifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) = enable()

    override fun onDestroy(owner: LifecycleOwner) = disable()

    private fun enable() {
        Log.v(TAG, "enable()")
        decorView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    private fun disable() {
        Log.v(TAG, "disable()")
        decorView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    companion object {
        val TAG = KeyboardStateObserver::class.java.simpleName
    }
}