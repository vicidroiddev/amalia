package com.vicidroid.amalia.ext

import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ui.BaseViewDelegate

// Place holder in case we should inject parameters from the activity.
inline fun <reified S : ViewState, E : ViewEvent> AppCompatActivity.viewDelegateProvider(crossinline viewDelegateCreator: () -> BaseViewDelegate<S, E>) =
    lazy {
        viewDelegateCreator()
    }