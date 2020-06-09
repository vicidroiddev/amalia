package com.vicidroid.amalia.ui.recyclerview.adapter

import android.content.Context
import androidx.lifecycle.LifecycleOwner

interface RecyclerViewAdapter {
    val lifecycleOwner: LifecycleOwner
    val context: Context
}
