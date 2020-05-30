package com.vicidroid.amalia.coroutine_scopes

import com.vicidroid.amalia.core.BasePresenter
import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

private const val MAIN_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:MainScope.JOB_KEY"
private const val IO_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:IOScope.JOB_KEY"
private const val DEFAULT_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:DefaultScope.JOB_KEY"
private const val IMMEDIATE_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:ImmediateScope.JOB_KEY"

/**
 * A coroutine scope which uses [Dispatchers.Main]
 * This coroutine job is automatically canceled when the presenter is destroyed
 */
val BasePresenter.mainScope: CoroutineScope
    get() {
        return scopeFor(Dispatchers.Main)
    }

val BasePresenter.immediate: CoroutineScope
    get() {
        return scopeFor(Dispatchers.Main.immediate)
    }

val BasePresenter.ioScope: CoroutineScope
    get() {
        return scopeFor(Dispatchers.IO)
    }

val BasePresenter.defaultScope: CoroutineScope
    get() {
        return scopeFor(Dispatchers.Default)
    }

private fun BasePresenter.scopeFor(dispatcher: CoroutineDispatcher): CoroutineScope {
    val key = when (dispatcher) {
        Dispatchers.Main -> MAIN_SCOPE_KEY
        Dispatchers.IO -> IO_SCOPE_KEY
        Dispatchers.Default -> DEFAULT_SCOPE_KEY
        Dispatchers.Main.immediate -> IMMEDIATE_SCOPE_KEY
        else -> error("Unknown dispatcher provided")
    }

    return this.closeableObjects.getOrPut(key) {
        AmaliaCloseableCoroutineScope(
            SupervisorJob() + dispatcher
        )
    } as CoroutineScope
}

/**
 * A CoroutineScope that implements [java.io.Closable]
 * Viewmodels will go through the `mBagOfTags` and call close as long as the object implements [Closable]
 */
private class AmaliaCloseableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : Closeable,
    CoroutineScope {
    override fun close() = coroutineContext.cancel()
}