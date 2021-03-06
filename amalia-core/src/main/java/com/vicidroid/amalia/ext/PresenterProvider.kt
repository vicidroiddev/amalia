package com.vicidroid.amalia.ext

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.vicidroid.amalia.core.BasePresenter

/**
 * Provides a Kotlin friendly method to obtain a presenter while in a fragment.
 * [hooks] can be leveraged to apply lateinit fields on a presenter.
 * This can be ideal when having to otherwise repeatedly pass in fields to the constructor.
 * Example: say your base fragment requires all fragments to have a uri that represents the current page.
 * Instead of manually passing this uri to each presenter via a constructor, you could apply it automatically via
 * your own presenterProvider with default [hooks]
 */
inline fun <reified P : BasePresenter> Fragment.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P
) =
    presenterProvider(this, this as SavedStateRegistryOwner, presenterCreator, hooks)

inline fun <reified P : BasePresenter> FragmentActivity.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P
) =
    presenterProvider(this, this as SavedStateRegistryOwner, presenterCreator, hooks)

inline fun <reified P : BasePresenter> AppCompatActivity.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P
) =
    presenterProvider(this, this as SavedStateRegistryOwner, presenterCreator, hooks)

inline fun <reified P : BasePresenter> LifecycleOwner.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P
) =
    presenterProvider(this, this.savedStateRegistryOwner, presenterCreator, hooks)

/**
 * Provides a child presenter descending from a parent presenter.
 * The parent presenter must be bound to a view delegate.
 * It would be ideal to leverage this in [#onBindViewDelegate(...)]
 */
inline fun <reified P : BasePresenter> BasePresenter.childPresenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P
) = lazy {
    viewLifecycleOwner ?: error("The parent presenter must be bound to a view delegate.")
    presenterCreator().also { childPresenter ->
        this.childPresenters += childPresenter
        hooks?.invoke(childPresenter)
        childPresenter.initializePresenter(applicationContext, savedStateHandle)
    }
}

/**
 * The persistence of this presenter will depend on the lifecycle owner.
 * When called on a fragment, presenter will be destroyed in fragment.onDestroy()
 * When called on an activity, presenter will be destroyed in activity.onDestroy()
 *
 * Note: Observers in view delegates connected to presenter states should be removed in fragment.onDestroyView().
 * The behaviour is based on the lifecycle owner passed to the view delegate.
 * This is done to support the second fragment lifecycle [onDetach] [onAttach]
 */
inline fun <reified P : BasePresenter> presenterProvider(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    crossinline presenterCreator: () -> P,
    noinline externalHooks: ((P) -> Unit)? = null,
    defaultArgs: Bundle? = null
) = lazy(LazyThreadSafetyMode.NONE) {

    val savedStateFactory = object : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel?> create(key: String, modelClass: Class<VM>, handle: SavedStateHandle): VM {
            return (presenterCreator() as VM).also { presenter ->
                (presenter as P).let {
                    externalHooks?.invoke(it)
                    presenter.initializePresenter(lifecycleOwner.application, handle)
                }
            }
        }
    }

    when (lifecycleOwner) {
        is FragmentActivity -> ViewModelProvider(lifecycleOwner, savedStateFactory)[P::class.java]
        is Fragment -> ViewModelProvider(lifecycleOwner, savedStateFactory)[P::class.java]
        else -> error("Unsupported lifecycle owner detected.")
    }
}

val LifecycleOwner.application: Application
    get() = when (this) {
        is FragmentActivity -> this.application
        is Fragment -> this.requireActivity().application
        else -> error("Unable to obtain context due to unsupported lifecycle owner.")
    }

val LifecycleOwner.savedStateRegistryOwner: SavedStateRegistryOwner
    get() = when (this) {
        is FragmentActivity -> this as SavedStateRegistryOwner
        is Fragment -> this as SavedStateRegistryOwner
        else -> error("Unable to obtain SavedStateRegistryOwner under Lifecycle owner. Ensure activities inherit from ComponentActivity.")
    }

