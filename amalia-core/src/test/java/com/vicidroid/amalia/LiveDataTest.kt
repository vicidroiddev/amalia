package com.vicidroid.amalia

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LiveDataTest : TestCase() {

  // Ensure live data emits immediately.
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  val lifecycleOwner = mock<LifecycleOwner>()
  val lifecycleRegistry = LifecycleRegistry(lifecycleOwner)

  @Before
  fun init() {
    whenever(lifecycleOwner.lifecycle).thenReturn(lifecycleRegistry)

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  @Test
  fun testLiveDataObservers() {
    val liveData = MutableLiveData<String>()
    val newValue = "Second"

    lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)

    val observer = Observer<String> {
      assertEquals(newValue, it)
    }

    liveData.observe(lifecycleOwner, observer)

    liveData.value = newValue

    lifecycleRegistry.markState(Lifecycle.State.STARTED)
  }
}