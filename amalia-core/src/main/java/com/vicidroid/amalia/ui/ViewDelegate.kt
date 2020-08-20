package com.vicidroid.amalia.ui

import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.core.EphemeralState
import com.vicidroid.amalia.core.ViewState
import kotlinx.coroutines.CoroutineScope

interface ViewDelegate {
    val viewDelegateLifecycleOwner: LifecycleOwner

    fun onBindViewDelegate() {}

    /**
     * Render a view state that is provided by the Presenter.
     * The view delegate updates the UI accordingly.
     */
    fun renderViewState(state: ViewState)

    /**
     * Handle an ephemeral state that is provided by the Presenter.
     * This state will not be retained, it is meant as a one-shot fire
     * to show short-lived states.
     * This is in contracts to a ViewState which represents a full snapshot in time, which
     * should represent your UI completely.
     */
    fun renderEphemeralState(state: EphemeralState) {

    }
}
