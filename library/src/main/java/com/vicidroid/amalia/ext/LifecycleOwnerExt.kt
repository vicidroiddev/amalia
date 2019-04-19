package com.vicidroid.amalia.ext

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner

val LifecycleOwner.applicationContext: Context
  get() = when (this) {
    is FragmentActivity -> this.application
    is Fragment -> this.activity!!.application
    else -> error("Unable to obtain context due to unsupported lifecycle owner.")
  }