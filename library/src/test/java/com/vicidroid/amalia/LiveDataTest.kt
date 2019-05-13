package com.vicidroid.amalia

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
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
class LiveDataTest : TestCase() {

  // Ensure live data emits immediately.
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Mock
  lateinit var lifecycleOwner: LifecycleOwner

  private lateinit var lifecycleRegistry: LifecycleRegistry

  @Before
  fun init() {
    MockitoAnnotations.initMocks(this)

    lifecycleRegistry = LifecycleRegistry(lifecycleOwner)
    whenever(lifecycleOwner.lifecycle).thenReturn(lifecycleRegistry)

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  @Test
  fun testLiveDataObservers() {
    val liveData = MutableLiveData<String>()
    val newValue = "Second"

    lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED

    val observer = spy(Observer<String> {
      assertEquals(newValue, it)
    })

    liveData.observe(lifecycleOwner, observer)

    liveData.value = newValue

    lifecycleRegistry.currentState = Lifecycle.State.STARTED
  }
}