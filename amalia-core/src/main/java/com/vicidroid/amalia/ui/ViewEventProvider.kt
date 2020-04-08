package com.vicidroid.amalia.ui

import com.vicidroid.amalia.core.ViewEvent

interface ViewEventProvider {
    fun propagateEventsTo(observer: (ViewEvent) -> Unit)
}
