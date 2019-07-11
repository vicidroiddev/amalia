package com.vicidroid.amalia.ui

import com.vicidroid.amalia.core.ViewEvent

interface ViewEventProvider<E : ViewEvent> {
    fun propagateEventsTo(observer: (E) -> Unit)
}
