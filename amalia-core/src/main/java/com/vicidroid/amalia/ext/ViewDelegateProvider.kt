package com.vicidroid.amalia.ext

import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ui.BaseViewDelegate

/**
 * Used to conveniently inject parameters from the activity into the view delegate.
 * Note: this `lazy` approach cannot be used by fragments due to the attach/detach lifecycle which will destroy the view but not the fragment instance.
 */
inline fun AppCompatActivity.viewDelegateProvider(crossinline viewDelegateCreator: () -> BaseViewDelegate) =
    lazy {
        viewDelegateCreator().also { delegate ->
            delegate.setHostActivity(this)
        }
    }

/**
 * Used for child view delegates. Allows injection of important fields such as [BaseViewDelegate.parent]
 */
inline fun BaseViewDelegate.viewDelegateProvider(crossinline viewDelegateCreator: () -> BaseViewDelegate) =
    lazy {
        viewDelegateCreator().also { delegate ->
            delegate.parent = this
        }
    }