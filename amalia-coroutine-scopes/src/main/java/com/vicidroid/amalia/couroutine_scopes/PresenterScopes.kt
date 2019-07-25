package androidx.lifecycle //we need to be in this package to access getTag in ViewModel

import com.vicidroid.amalia.core.BasePresenter
import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

private const val MAIN_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:MainScope.JOB_KEY"
private const val IO_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:IOScope.JOB_KEY"
private const val DEFAULT_SCOPE_KEY = "com.vicidroid.amalia.core.PresenterScopes:DefaultScope.JOB_KEY"

/**
 * A coroutine scope which uses [Dispatchers.Main]
 * This coroutine job is automatically canceled when the presenter is destroyed
 */
val BasePresenter<*, *>.mainScope: CoroutineScope
    get() {
        return (this as ViewModel).scopeFor(Dispatchers.Main)
    }

val BasePresenter<*, *>.ioScope: CoroutineScope
    get() {
        return (this as ViewModel).scopeFor(Dispatchers.IO)
    }

val BasePresenter<*, *>.defaultScope: CoroutineScope
    get() {
        return (this as ViewModel).scopeFor(Dispatchers.Default)
    }

private fun ViewModel.scopeFor(dispatcher: CoroutineDispatcher): CoroutineScope {
    val key = when (dispatcher) {
        Dispatchers.Main -> MAIN_SCOPE_KEY
        Dispatchers.IO -> IO_SCOPE_KEY
        Dispatchers.Default -> DEFAULT_SCOPE_KEY
        else -> error("Unknown dispatcher provided")
    }

    return this.getTag(key) ?: this.setTagIfAbsent(
        key,
        CloseableCoroutineScope(SupervisorJob() + dispatcher)
    )
}

/**
 * A CoroutineScope that implements [java.io.Closable]
 * Viewmodels will go through the `mBagOfTags` and call close as long as the object implements [Closable]
 */
private class CloseableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : Closeable,
    CoroutineScope {
    override fun close() = coroutineContext.cancel()
}