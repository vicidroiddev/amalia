package com.vicidroid.amalia.ui

import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.core.ViewState

interface ViewDelegate {
    val viewDelegateLifecycleOwner: LifecycleOwner

    fun onBindViewDelegate() {}

    /**
     * Render a view state that is provided by the Presenter.
     * The view delegate updates the UI accordingly.
     */
    fun renderViewState(state: ViewState)
}
