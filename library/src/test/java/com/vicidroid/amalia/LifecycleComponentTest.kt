package com.vicidroid.amalia

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import com.nhaarman.mockitokotlin2.*
import com.vicidroid.amalia.core.LifecycleComponent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ext.componentProvider
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
class LifecycleComponentTest : TestCase() {

  // Ensure live data emits immediately.
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Mock
  lateinit var activity: FragmentActivity

  @Mock
  lateinit var application: Application

  @Mock
  lateinit var viewModelStore: ViewModelStore

  @Mock
  lateinit var lifecycleOwner: LifecycleOwner

  lateinit var lifecycle: LifecycleRegistry

  lateinit var mockedComponent: FakeComponent

  lateinit var componentViaProvider: LifecycleComponent<ViewState>

  @Before
  fun init() {
    MockitoAnnotations.initMocks(this)

    lifecycle = spy(LifecycleRegistry(lifecycleOwner))
    mockedComponent = spy(FakeComponent())

    whenever(lifecycleOwner.lifecycle).thenReturn(lifecycle)

    whenever(activity.lifecycle).thenReturn(lifecycle)
    whenever(activity.application).thenReturn(application)
    whenever(activity.viewModelStore).thenReturn(viewModelStore)

    componentViaProvider = activity.componentProvider { FakeComponent() }.value
  }

  @Test
  fun `lifecycle dependencies mocked correctly`() {
    assertEquals(lifecycle, activity.lifecycle)
    assertEquals(componentViaProvider.lifecycleOwner, activity)
  }

  @Test
  fun `component is added as lifecycle observer when using component provider`() {
    verify(lifecycle).addObserver(componentViaProvider)
    assertEquals(lifecycle.observerCount, 1)
  }

  @Test
  fun `component is not added as observer to lifecycle callbacks without provider`() {
    val component = FakeComponent()
     verify(lifecycle, never()).addObserver(component)
  }

  @Test
  fun `component receives lifecycle callbacks`() {
    mockedComponent.lifecycleOwner = lifecycleOwner

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    verify(mockedComponent).onCreate(lifecycleOwner)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    verify(mockedComponent).onStart(lifecycleOwner)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    verify(mockedComponent).onResume(lifecycleOwner)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    verify(mockedComponent).onPause(lifecycleOwner)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    verify(mockedComponent).onStop(lifecycleOwner)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    verify(mockedComponent).onDestroy(lifecycleOwner)
  }

  @Test
  fun `component destroys lifecycle owner`() {
    mockedComponent.lifecycleOwner = lifecycleOwner
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    assertNull(mockedComponent.lifecycleOwner)
  }

  class FakeComponent : LifecycleComponent<ViewState>()
}