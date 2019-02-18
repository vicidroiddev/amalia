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

inline fun <reified P : BasePresenter<*, *>> Fragment.presenterProvider(
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator)

inline fun <reified P : BasePresenter<*, *>> FragmentActivity.presenterProvider(
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator)

inline fun <reified P : BasePresenter<*, *>> AppCompatActivity.presenterProvider(
    crossinline presenterCreator: () -> P) =
    presenterProvider(this, presenterCreator)

/**
 * Provides a child presenter descending from a parent presenter.
 * The parent presenter must be bound to a view delegate.
 * It would be ideal to leverage this in [#onBindViewDelegate(...)]
 */
inline fun <reified P : BasePresenter<*, *>> BasePresenter<*, *>.childPresenterProvider(
    crossinline presenterCreator: () -> P): P {
  lifecycleOwner ?: error("The parent presenter must be bound to a view delegate.")
  return presenterCreator().apply { applicationContext = this@childPresenterProvider.applicationContext }
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
    crossinline presenterCreator: () -> P) = lazy(LazyThreadSafetyMode.NONE) {

  @Suppress("UNCHECKED_CAST")
  val factory = object : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(presenterClazz: Class<VM>): VM {
      return (presenterCreator() as VM).apply {
        (this as BasePresenter<*, *>).applicationContext = lifecycleOwner.applicationContext
      }
    }
  }

  when (lifecycleOwner) {
    is FragmentActivity -> ViewModelProviders.of(lifecycleOwner, factory)[P::class.java]
    is Fragment -> ViewModelProviders.of(lifecycleOwner, factory)[P::class.java]
    else -> error("Unsupported lifecycle owner detected.")
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
class PresenterProvider<P : BasePresenter<*, *>>(val fragment: Fragment, val presenterCreator: () -> BasePresenter<*, *>) {
  @Suppress("UNCHECKED_CAST")
  fun provide(): P {
    return fragment.presenterProvider { presenterCreator() }.value as P
  }
}
