package com.vicidroid.amalia

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ext.presenterProvider
import com.vicidroid.amalia.ui.BaseViewDelegate
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
class BasePresenterTest : TestCase() {

  // Ensure live data emits immediately.
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Mock
  lateinit var view: View

  @Mock
  lateinit var activity: FragmentActivity

  @Mock
  lateinit var application: Application

  @Mock
  lateinit var viewModelStore: ViewModelStore

  @Mock
  lateinit var context: FragmentActivity

  @Mock
  lateinit var lifecycleOwner: LifecycleOwner

  private lateinit var lifecycle: LifecycleRegistry

  private val viewEvent = FakeViewEvent()
  private val viewState = FakeViewState()

  private lateinit var presenter: BasePresenter<ViewState, ViewEvent>
  private lateinit var parentPresenter: FakeParentPresenter

  lateinit var viewDelegate: BaseViewDelegate<ViewState, ViewEvent>

  @Before
  fun init() {
    MockitoAnnotations.initMocks(this)

    presenter = spy(FakePresenter())
    parentPresenter = spy(FakeParentPresenter())
    lifecycle = spy(LifecycleRegistry(lifecycleOwner))

    whenever(lifecycleOwner.lifecycle).thenReturn(lifecycle)

    whenever(view.context).thenReturn(activity)

    whenever(activity.lifecycle).thenReturn(lifecycle)
    whenever(activity.application).thenReturn(application)
    whenever(activity.viewModelStore).thenReturn(viewModelStore)
    whenever(view.context).thenReturn(context)

    viewDelegate = spy(FakeViewDelegate(lifecycleOwner, view))

    whenever(viewDelegate.lifecycle).thenReturn(lifecycle)

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  @Test
  fun `presenter becomes lifecycle aware`() {
    bindPresenter()

    lifecycle.markState(Lifecycle.State.CREATED)
    assertNotNull(presenter.lifecycleOwner)

    verify(lifecycle).addObserver(presenter)
    verify(presenter).onCreate(lifecycleOwner)
    verify(presenter).onBindViewDelegate(viewDelegate)
  }

  @Test
  fun `presenter cleans up resources`() {
    bindPresenter()
    lifecycle.markState(Lifecycle.State.DESTROYED)
    verify(presenter).onDestroy(lifecycleOwner)
    assertNull(presenter.lifecycleOwner)
  }

  @Test
  fun `view state not caught before binding`() {
    presenter.pushState(viewState)
    verify(viewDelegate, never()).renderViewState(viewState)
    bindPresenter()
    verify(viewDelegate).renderViewState(viewState)
  }

  @Test
  fun `view state caught by viewDelegate`() {
    bindPresenter()
    presenter.pushState(viewState)
    verify(viewDelegate).renderViewState(viewState)
  }

  @Test
  fun `view state propogated to parent presenter`() {
    bindPresenter()

    parentPresenter.childPresenter = presenter
    parentPresenter.propagate()

    presenter.pushState(viewState)

    verify(parentPresenter).onStatePropagated(viewState)
  }

  @Test
  fun `view event not caught before binding`() {
    viewDelegate.pushEvent(viewEvent)
    verify(presenter, never()).onViewEvent(viewEvent)
    bindPresenter()
    verify(presenter).onViewEvent(viewEvent)
  }

  @Test
  fun `view event caught by presenter`() {
    bindPresenter()
    viewDelegate.pushEvent(viewEvent)
    verify(presenter).onViewEvent(viewEvent)
  }

  @Test
  fun `applies hooks to shared base presenter`() {
    val currentUri = Uri.Builder()
        .scheme("appName")
        .authority("com.vicidroiddev.amalia")
        .appendPath("person")
        .appendPath("1")
        .appendPath("module")
        .appendPath("profile")
        .build()

    val hooks: ((BasePresenter<*, *>) -> Unit)? = {
      (it as SharedBasePresenter).currentUri = currentUri
    }

    lifecycle.markState(Lifecycle.State.CREATED)

    val presenter by activity.presenterProvider(hooks) {
      FakePresenter()
    }

    assertEquals(presenter.currentUri, currentUri)
  }

  private fun bindPresenter() {
    presenter.bind(viewDelegate)
  }

  class FakeViewEvent : ViewEvent
  class FakeViewState : ViewState

  interface SharedImportantFields {
    var currentUri: Uri
  }

  abstract class SharedBasePresenter
    : BasePresenter<ViewState, ViewEvent>(), SharedImportantFields {
    override lateinit var currentUri: Uri
  }

  class FakePresenter : SharedBasePresenter() {
    override fun onViewEvent(event: ViewEvent) {}
  }

  class FakeParentPresenter : BasePresenter<ViewState, ViewEvent>() {
    lateinit var childPresenter: BasePresenter<ViewState, ViewEvent>

    fun propagate() {
      childPresenter.propagateStatesTo(::onStatePropagated)
    }

    fun onStatePropagated(state: ViewState) {}
  }

  class FakeViewDelegate(lifecycleOwner: LifecycleOwner, rootView: View)
    : BaseViewDelegate<ViewState, ViewEvent>(lifecycleOwner, rootView) {
    override fun onSaveInstanceState(outState: Bundle) {}
    override fun renderViewState(state: ViewState) {}
  }
}