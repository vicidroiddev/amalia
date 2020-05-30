package com.vicidroid.amalia

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.nhaarman.mockitokotlin2.*
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ext.childPresenterProvider
import com.vicidroid.amalia.ext.presenterProvider
import com.vicidroid.amalia.ui.BaseViewDelegate
import com.vicidroid.amalia.ui.ViewDelegate
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.Closeable


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

    @Mock
    lateinit var savedStateRegistry: SavedStateRegistry

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    private lateinit var lifecycle: LifecycleRegistry

    private val viewEvent = FakeViewEvent()
    private val viewState = FakeViewState()

    private lateinit var presenter: BasePresenter
    private lateinit var parentPresenter: FakeParentPresenter

    lateinit var viewDelegate: BaseViewDelegate

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        presenter = spy(FakePresenter())
        parentPresenter = spy(FakeParentPresenter())
        lifecycle = spy(LifecycleRegistry(lifecycleOwner))

        whenever(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        whenever(view.context).thenReturn(activity)

        whenever(activity.lifecycle).thenReturn(lifecycle)
        whenever((activity as SavedStateRegistryOwner).savedStateRegistry).thenReturn(savedStateRegistry)
        whenever(activity.application).thenReturn(application)
        whenever(activity.viewModelStore).thenReturn(viewModelStore)
        whenever(view.context).thenReturn(context)

        viewDelegate = spy(FakeViewDelegate(lifecycleOwner, view))

        whenever(viewDelegate.lifecycle).thenReturn(lifecycle)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @Test
    fun `presenter is lifecycle aware upon creation`() {
        val presenter = spy(activity.presenterProvider { (FakePresenter()) }.value)
        presenter.bind(viewDelegate)

        assertNotNull(presenter.viewLifecycleOwner)
        verify(lifecycle).addObserver(presenter.viewLifecycleObserver)
        verify(presenter).onViewAttached(lifecycleOwner)
    }

    @Test
    fun `presenter is lifecycle aware upon destruction`() {
        bindPresenter()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        assertTrue(viewDelegate.viewDelegateLifecycleOwner == lifecycleOwner)
        verify(presenter).onViewDetached(lifecycleOwner)
        assertNull(presenter.viewLifecycleOwner)
        assertEquals(lifecycle.observerCount, 0)
    }

    @Test
    fun `presenter is lifecycle aware when using bind without view delegate`() {
        val presenter = spy(FakePresenter())
        lifecycle.markState(Lifecycle.State.CREATED)
        presenter.bind(lifecycleOwner)

        verify(presenter).onBindViewLifecycleOwner(lifecycleOwner)
        verify(presenter).onViewAttached(lifecycleOwner)
        verify(lifecycle).addObserver(presenter.viewLifecycleObserver)
        assertNotNull(presenter.viewLifecycleOwner)

        lifecycle.markState(Lifecycle.State.DESTROYED)
        verify(presenter).onViewDetached(lifecycleOwner)
        assertNull(presenter.viewLifecycleOwner)
    }

    @Test
    fun `throws exception when bind is performed twice with lifecycle owner`() {
        val presenter = spy(FakePresenter())
        presenter.bind(lifecycleOwner)

        var thrownException: Exception? = null

        try {
            presenter.bind(lifecycleOwner)
        } catch (e: Exception) {
            thrownException = e
        } finally {
            assertEquals(thrownException!!.message, "Second call to bind() is suspicious.")
        }
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
    fun `view state caught by provided observer`() {
        val observer = spy (object: (ViewState) -> Unit {
            override fun invoke(p1: ViewState) {
            }
        })

        presenter.bind(lifecycleOwner, observer)
        presenter.pushState(viewState)
        verify(observer).invoke(viewState)
    }

    @Test
    fun `view state propagated to parent presenter`() {
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
    fun `viewdelegate receives bind callback`() {
        bindPresenter()
        verify(viewDelegate).onBindViewDelegate()
    }

    @Test
    fun `ensure order of initialization prior to loadInitialState`() {
        val hooks: ((FakePresenterWithUri) -> Unit) = {
            it.currentUri = Uri.EMPTY
        }

        val mockedHooks = spy(hooks)

        val presenter = spy(FakePresenterWithUri())

        activity.presenterProvider(mockedHooks) { presenter }.value

        val inorder = inOrder(mockedHooks, presenter)

        inorder.verify(mockedHooks).invoke(presenter)
        inorder.verify(presenter).initializePresenter(eq(application), any() )
        inorder.verify(presenter).loadInitialState()
    }

    @Test
    fun `applies hooks to shared base presenter`() {
        val currentUri = buildUri()

        val hooks: ((FakePresenterWithUri) -> Unit) = {
            it.currentUri = currentUri
        }

        val mockedHooks = spy(hooks)

        val presenterThatAccessesUriEarly = spy(FakePresenterWithUri())

        activity.presenterProvider(mockedHooks) { presenterThatAccessesUriEarly }.value

        verify(presenterThatAccessesUriEarly).loadInitialState()
        verify(mockedHooks).invoke(presenterThatAccessesUriEarly)
        assertEquals(presenterThatAccessesUriEarly.currentUri, currentUri)
    }

    @Test
    fun `applies hooks to child presenter`() {
        val currentUri = buildUri()

        val hooks: ((SharedBasePresenter) -> Unit) = {
            it.currentUri = currentUri
        }

        val mockedHooks = spy(hooks)

        val presenterThatAccessesUriEarly = spy(FakePresenterWithUri())
        activity.presenterProvider(mockedHooks) { presenterThatAccessesUriEarly }.value

        presenterThatAccessesUriEarly.bind(viewDelegate)

        val childPresenterThatAccessesUriEarly = spy(ChildFakePresenterWithUri())
        presenterThatAccessesUriEarly.childPresenterProvider(mockedHooks) { childPresenterThatAccessesUriEarly }.value

        verify(childPresenterThatAccessesUriEarly).loadInitialState()
        verify(mockedHooks).invoke(childPresenterThatAccessesUriEarly)
        assertEquals(childPresenterThatAccessesUriEarly.currentUri, currentUri)
    }

    @Test
    fun `binds presenter after using presenter provider`() {
        val presenter = spy(activity.presenterProvider { (FakePresenter()) }.value)
        presenter.bind(viewDelegate)
        verify(presenter).onBindViewDelegate(viewDelegate)
        verify(presenter).onBindViewLifecycleOwner(lifecycleOwner)
    }

    @Test
    fun `calls onPresenterDestroyed when lifecycle activity is destroyed`() {
        val controller = Robolectric.buildActivity(FragmentActivity::class.java).setup()
        val activity = controller.get()
        val parentPresenter = activity.presenterProvider { spy(FakeParentPresenter()) }.value

        controller.destroy()
        verify(parentPresenter).onPresenterDestroyed()
    }

    @Test
    fun `child presenter provider leverages parent fields`() {
        val parentPresenter by activity.presenterProvider {
            FakeParentPresenter()
        }

        parentPresenter.bind(viewDelegate)

        val childPresenter = parentPresenter.childPresenterProvider { FakePresenter() }.value
        childPresenter.bind(viewDelegate)

        Assert.assertEquals(childPresenter.viewLifecycleOwner, viewDelegate.viewDelegateLifecycleOwner)
        Assert.assertEquals(childPresenter.applicationContext, activity.application)
    }

    @Test
    fun `child presenter should receive onPresenterDestroyed if parent fires onPresenterDestroyed`() {
        val parentPresenter = activity.presenterProvider { FakeParentPresenter() }.value.also { it.bind(viewDelegate) }
        val spyChild = parentPresenter.childPresenterProvider { spy(FakePresenter()) }.value

        Assert.assertEquals(parentPresenter.childPresenters, listOf(spyChild))

        parentPresenter.onPresenterDestroyedInternal()
        verify(spyChild, times(1)).onPresenterDestroyedInternal()
    }

    @Test
    fun `presenter calls close on closeable object cache when presenter is destroyed`() {
        val closeableObj = spy(object : Closeable {
            var closed = false
            override fun close() {
                closed = true
            }
        })

        presenter.closeableObjects["my_key"] = closeableObj

        presenter.onPresenterDestroyedInternal()

        verify(closeableObj).close()
        Assert.assertTrue(closeableObj.closed)
    }

    @Test
    fun `presenter injects save state handle`() {
        val presenter by activity.presenterProvider { FakePresenter() }
        assertNotNull(presenter.savedStateHandle)
    }

    @Test
    fun `presenter binds to simple view delegate interface implementation`() {
        val viewDelegateImpl = spy(object: ViewDelegate {
            override val viewDelegateLifecycleOwner = lifecycleOwner
            override fun renderViewState(state: ViewState) {
            }
        })

        val presenter by activity.presenterProvider { FakePresenter() }
        val fakeState = FakeViewState()

        presenter.bind(viewDelegateImpl)
        presenter.pushState(fakeState)

        verify(viewDelegateImpl).renderViewState(fakeState)
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
        : BasePresenter(), SharedImportantFields {
        override lateinit var currentUri: Uri
    }

    class FakePresenter : SharedBasePresenter() {
        override fun loadInitialState() {
        }

        override fun onViewEvent(event: ViewEvent) {}
    }

    class FakePresenterWithUri : SharedBasePresenter() {
        override fun loadInitialState() {
            //Access currentUri
            this.currentUri
        }

        override fun onViewEvent(event: ViewEvent) {}
    }

    class ChildFakePresenterWithUri : SharedBasePresenter() {
        override fun loadInitialState() {
            //Access currentUri
            this.currentUri
        }

        override fun onViewEvent(event: ViewEvent) {}
    }

    class FakeParentPresenter : BasePresenter() {
        override fun loadInitialState() {
        }

        lateinit var childPresenter: BasePresenter

        fun propagate() {
            childPresenter.propagateStatesTo(::onStatePropagated)
        }

        fun onStatePropagated(@Suppress("UNUSED_PARAMETER") state: ViewState) {}
    }

    class FakeViewDelegate(lifecycleOwner: LifecycleOwner, rootView: View) :
        BaseViewDelegate(lifecycleOwner, rootView) {
        override fun renderViewState(state: ViewState) {}
    }

    private fun buildUri(): Uri {
        return Uri.Builder()
            .scheme("appName")
            .authority("com.vicidroiddev.amalia")
            .appendPath("person")
            .appendPath("1")
            .appendPath("module")
            .appendPath("profile")
            .build()
    }
}