package com.vicidroid.amalia.ui

import android.view.View
import androidx.lifecycle.LifecycleOwner

data class DelegateComponents(
    val viewLifecycleOwner: LifecycleOwner,
    val rootView: View
)