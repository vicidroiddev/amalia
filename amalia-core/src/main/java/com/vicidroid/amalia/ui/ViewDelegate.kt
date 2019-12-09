package com.vicidroid.amalia.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState

interface ViewDelegate<S : ViewState, E : ViewEvent> : ViewDelegateLifecycleCallbacks  {

    val viewDelegateLifecycleOwner: LifecycleOwner

    /**
     * Render a view state that is provided by the Presenter.
     * The view delegate updates the UI accordingly.
     */
    fun renderViewState(state: S)
}
