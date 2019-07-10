package com.vicidroid.amalia.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.vicidroid.amalia.core.ViewEvent

interface ViewEventProvider<E : ViewEvent> {
    fun observeEvents(lifecycleOwner: LifecycleOwner, observer: (E) -> Unit)
}
