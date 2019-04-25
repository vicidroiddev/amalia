package com.vicidroid.amalia.core

import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*

/**
 * Backed by Android's ViewModel in order to easily survive configuration changes.
 */
abstract class LifecycleComponent<S : ViewState>
    : ViewModel(),
    DefaultLifecycleObserver {

    lateinit var applicationContext: Context

    /**
     * Upon setting an owner this component will be added as an observer to receive lifecycle events.
     * When the owner is set to null we remove the observer.
     * In theory we should not need to remove the observer, but just in case we do it here.
     * See: https://github.com/googlecodelabs/android-lifecycles/issues/5
     */
    var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            value?.lifecycle?.addObserver(this)
                ?: field?.lifecycle?.removeObserver(this)

            field = value
        }

    private val viewStateLiveData = MutableLiveData<S>()

    fun stateLiveData(): LiveData<S> = viewStateLiveData

    /**
     * This may be of use for legacy code where one wishes to separate some data loading logic
     * from a fragment and reap the benefits of components for lifecycle aware behaviour without
     * manipulating the view logic.
     */
    @Deprecated(message = "Leverage a provider which injects a lifecycle", replaceWith = ReplaceWith("propagateStatesTo(observer)"))
    open fun propagateStatesTo(lifecycleOwner: LifecycleOwner, observer: (S) -> Unit) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
        stateLiveData().observe(lifecycleOwner, Observer { observer(it) })
    }

    /**
     * Propagate pushes states to another via the provided observer.
     * This may be of use when adding amalia to legacy code or in a parent child presenter hierarchy.
     */
    fun propagateStatesTo(observer: (S) -> Unit) {
        lifecycleOwner ?: error("You must call bind() prior to propagating states. Alternatively you must provide a lifecycle.")
        stateLiveData().observe(lifecycleOwner!!, Observer { observer(it) })
    }

    /**
     * Sends a [state] for the view delegate to process in order to reflect UI changes.
     * This must be called from the main thread.
     */
    @UiThread
    fun pushState(state: S) {
        viewStateLiveData.value = state
    }

    /**
     * Sends a [state] for the view delegate to process in order to reflect UI changes.
     * This may be called from a background thread.
     */
    @WorkerThread
    fun pushStateOnMainLooper(state: S) {
        viewStateLiveData.postValue(state)
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        // When the provided lifecycle owner goes through onDestroy, let's ensure we avoid any leaking.
        lifecycleOwner = null
    }

    override fun onCleared() {
        //https://github.com/googlecodelabs/android-lifecycles/issues/5
        // We do not have to remove the observer to the LifecycleRegistry according to the above
        // If this changes we should clear out our observer manually.
    }
}