package com.vicidroid.amalia.ui

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BaseViewDelegateTest : TestCase() {

    // Ensure live data emits immediately.
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner

    @Mock
    lateinit var view: View

    @Mock
    lateinit var context: Context

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        whenever(view.context).thenReturn(context)
    }

    @Test
    fun onInterceptEventChain() {
        val delegate =
            spy(object : BaseViewDelegate(lifecycleOwner, view) {
                override fun renderViewState(state: ViewState) {

                }

                override fun onInterceptEventChain(event: ViewEvent) {
                    (event as DefaultViewEvent).source = "intercepted"
                }
            })

        val event = DefaultViewEvent()

        delegate.pushEvent(event)
        verify(delegate).onInterceptEventChain(event)

        assertEquals(event.source, "intercepted")
    }

    open class DefaultViewEvent : ViewEvent {
        lateinit var source: String
    }
}