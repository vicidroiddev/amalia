package com.vicidroid.amalia.ui.recyclerview

import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem

sealed class AmaliaCommonEvent : ViewEvent {
    class NewVisibleItemsDetected(val visibleItems: Set<DiffItem>) : AmaliaCommonEvent()
}