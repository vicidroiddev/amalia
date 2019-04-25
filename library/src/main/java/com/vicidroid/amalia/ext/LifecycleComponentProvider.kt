package com.vicidroid.amalia.ext

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.LifecycleComponent


inline fun <reified C : LifecycleComponent<*>> Fragment.componentProvider(crossinline componentCreator: () -> C) =
    componentProvider(this, componentCreator)

inline fun <reified P : LifecycleComponent<*>> FragmentActivity.componentProvider(crossinline presenterCreator: () -> P) =
    componentProvider(this, presenterCreator)

inline fun <reified P : LifecycleComponent<*>> AppCompatActivity.componentProvider(crossinline presenterCreator: () -> P) =
    componentProvider(this, presenterCreator)


inline fun <reified C : LifecycleComponent<*>> componentProvider(
    lifecycleOwner: LifecycleOwner,
    crossinline componentCreator: () -> C
) = lazy(LazyThreadSafetyMode.NONE) {

    @Suppress("UNCHECKED_CAST")
    val factory = object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(componentClazz: Class<VM>) = componentCreator() as VM
    }

    when (lifecycleOwner) {
        is FragmentActivity -> ViewModelProviders.of(lifecycleOwner, factory)[C::class.java]
        is Fragment -> ViewModelProviders.of(lifecycleOwner, factory)[C::class.java]
        else -> error("Unsupported lifecycle owner detected.")
    }.also { c ->
        c.lifecycleOwner = lifecycleOwner
        c.applicationContext = lifecycleOwner.applicationContext
    }
}