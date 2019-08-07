package com.vicidroid.amalia.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class ViewEventStore<E : ViewEvent> {
    private val viewEventLiveData: MutableLiveData<E> = MutableLiveData()

    fun pushEvent(event: E) {
        viewEventLiveData.value = event
    }

    fun observe(owner: LifecycleOwner, observer: (E) -> Unit) {
        viewEventLiveData.observe(owner, Observer<E> { observer.invoke(it!!) })
    }
}