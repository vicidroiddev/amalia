package com.vicidroid.amalia.ui.recyclerview

import androidx.lifecycle.LifecycleOwner

interface RecyclerViewAdapter {
    abstract val lifecycleOwner: LifecycleOwner
    abstract val viewDelegate: RecyclerViewDelegate<*, *>
}
