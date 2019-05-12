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

/**
 * Provides a Kotlin friendly method to obtain a presenter while in a fragment.
 * [hooks] can be leveraged to apply lateinit fields on a presenter.
 * This can be ideal when having to otherwise repeatedly pass in fields to the constructor.
 * Example: say your base fragment requires all fragments to have a uri that represents the current page.
 * Instead of manually passing this uri to each presenter via a constructor, you could apply it automatically via
 * your own presenterProvider with default [hooks]
 */
inline fun <reified P : BasePresenter<*, *>> Fragment.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator,  hooks)

inline fun <reified P : BasePresenter<*, *>> FragmentActivity.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator, hooks)

inline fun <reified P : BasePresenter<*, *>> AppCompatActivity.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator, hooks)

inline fun <reified P : BasePresenter<*, *>> LifecycleOwner.presenterProvider(
    noinline hooks: ((P) -> Unit)? = null,
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator, hooks)

/**
 * Provides a child presenter descending from a parent presenter.
 * The parent presenter must be bound to a view delegate.
 * It would be ideal to leverage this in [#onBindViewDelegate(...)]
 */
inline fun <reified P : BasePresenter<*, *>> BasePresenter<*, *>.childPresenterProvider(
    crossinline presenterCreator: () -> P) = lazy {
      viewLifecycleOwner ?: error("The parent presenter must be bound to a view delegate.")
      presenterLifecycleOwner ?: error("The parent presenter must have been initialized.")
      presenterCreator().also { childPresenter ->
          childPresenter.applicationContext = applicationContext
          childPresenter.presenterLifecycleOwner = presenterLifecycleOwner
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
inline fun <reified P : BasePresenter<*, *>> presenterProvider(
    lifecycleOwner: LifecycleOwner,
    crossinline presenterCreator: () -> P,
    noinline externalHooks: ((P) -> Unit)? = null) = lazy(LazyThreadSafetyMode.NONE) {

  @Suppress("UNCHECKED_CAST")
  val factory = object : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(presenterClazz: Class<VM>): VM {
      return (presenterCreator() as VM).also { presenter ->
        (presenter as P).let {
          presenter.applicationContext = lifecycleOwner.applicationContext
          externalHooks?.invoke(it)
        }
      }
    }
  }

  when (lifecycleOwner) {
    is FragmentActivity -> ViewModelProviders.of(lifecycleOwner, factory)[P::class.java]
    is Fragment -> ViewModelProviders.of(lifecycleOwner, factory)[P::class.java]
    else -> error("Unsupported lifecycle owner detected.")
  }.also { p ->
      p.presenterLifecycleOwner = lifecycleOwner
  }
}

val LifecycleOwner.applicationContext: Context
  get() = when (this) {
    is FragmentActivity -> this.application
    is Fragment -> this.activity!!.application
    else -> error("Unable to obtain context due to unsupported lifecycle owner.")
  }

/**
 * Workaround for reified types which allows the use of [presenterProvider] in Java.
 * Moreover, this will support constructor arguments in presenters.
 */

object PresenterProvider {
  @Suppress("UNCHECKED_CAST")
  @JvmStatic
  fun <P : BasePresenter<*,*>> provide(fragment: Fragment, presenterCreator: () -> BasePresenter<*, *>): P {
    return fragment.presenterProvider { presenterCreator() }.value as P
  }
}