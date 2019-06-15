package com.vicidroid.amalia.core.persistance

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle

interface PersistableState {
    var savedStateHandle: SavedStateHandle

    fun <V> consumePersisted(key: String): V {
        return savedStateHandle.remove(key) ?: error("$key could not be found.")
    }

    fun <V> consumePersistedOrDefault(key: String, defaultValue: () -> V): V {
        return savedStateHandle.remove(key) ?: defaultValue.invoke()
    }

    fun <V> consumePersistedOrNull(key: String): V? {
        return savedStateHandle.remove(key)
    }

    fun <V> persist(key: String, value: V) {
        savedStateHandle[key] = value
    }

    fun persistViewState(owner: String, state: Parcelable) {
        savedStateHandle[viewStateKey(owner)] = state
    }

    fun viewStateKey(owner: String) = "${owner}_view_state"
}
