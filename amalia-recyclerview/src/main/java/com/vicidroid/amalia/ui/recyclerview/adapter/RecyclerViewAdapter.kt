package com.vicidroid.amalia.ui.recyclerview.adapter

import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewDelegate

interface RecyclerViewAdapter {
    abstract val lifecycleOwner: LifecycleOwner
    abstract val viewDelegate: RecyclerViewDelegate<*, *>
}
