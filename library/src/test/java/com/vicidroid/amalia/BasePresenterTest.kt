package com.vicidroid.amalia

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistry
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

    @Mock
    lateinit var savedStateRegistry: SavedStateRegistry

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
        whenever(activity.savedStateRegistry).thenReturn(savedStateRegistry)
        whenever(activity.application).thenReturn(application)
        whenever(activity.viewModelStore).thenReturn(viewModelStore)
        whenever(view.context).thenReturn(context)

        viewDelegate = spy(FakeViewDelegate(lifecycleOwner, view))

        whenever(viewDelegate.lifecycle).thenReturn(lifecycle)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @Test
    fun `presenter is viewdelegate lifecycle aware upon creation`() {
        bindPresenter()
        assertNotNull(presenter.viewLifecycleOwner)
        verify(lifecycle).addObserver(presenter.viewLifecycleObserver)
        verify(presenter).onViewAttached(lifecycleOwner)
    }

    @Test
    fun `presenter is viewdelegate lifecycle aware upon destruction`() {
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
        lifecycle.currentState = Lifecycle.State.CREATED
        presenter.bindViewLifecycleOwner(lifecycleOwner)

        verify(presenter).onBindViewLifecycleOwner(lifecycleOwner)
        verify(presenter).onViewAttached(lifecycleOwner)
        verify(lifecycle).addObserver(presenter.viewLifecycleObserver)
        assertNotNull(presenter.viewLifecycleOwner)

        lifecycle.currentState = Lifecycle.State.DESTROYED
        verify(presenter).onViewDetached(lifecycleOwner)
        assertNull(presenter.viewLifecycleOwner)
    }

    @Test
    fun `throws exception when bind is performed with view delegate`() {
        val presenter = spy(FakePresenter())
        presenter.bind(viewDelegate)

        var thrownException: Exception? = null

        try {
            presenter.bind(viewDelegate)
        } catch (e: Exception) {
            thrownException = e
        } finally {
            assertEquals(thrownException!!.message, "Second call to bind() is suspicious.")
        }
    }

    @Test
    fun `throws exception when bind is performed twice with lifecycle owner`() {
        val presenter = spy(FakePresenter())
        presenter.bindViewLifecycleOwner(lifecycleOwner)

        var thrownException: Exception? = null

        try {
            presenter.bindViewLifecycleOwner(lifecycleOwner)
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
    fun `ensure hooks are applied before loadInitialState is called`() {
        val presenter by activity.presenterProvider {
            FakePresenter()
        }
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

        val hooks: ((FakePresenterWithUri) -> Unit)? = {
            it.currentUri = currentUri
        }

        val mockedHooks = spy(hooks)

        lifecycle.currentState = Lifecycle.State.CREATED

        val presenterThatAccessesUriEarly = spy(FakePresenterWithUri())

        activity.presenterProvider(mockedHooks) { presenterThatAccessesUriEarly }.value

        verify(presenterThatAccessesUriEarly).loadInitialState()
        verify(mockedHooks)!!.invoke(presenterThatAccessesUriEarly)
        assertEquals(presenterThatAccessesUriEarly.currentUri, currentUri)
    }

    @Test
    fun `binds presenter after using presenter provider`() {
        val presenter = spy(activity.presenterProvider { (FakePresenter()) }.value)
        presenter.bind(viewDelegate)
        verify(presenter).onBindViewDelegate(viewDelegate)
        verify(presenter).onBindViewLifecycleOwner(lifecycleOwner)
        assertNotNull(presenter.presenterLifecycleOwner)
    }

//    // This fails because the activity is mocked and won't actually go through the regular handle lifecycle events.
//    // IN the end onCleared is not called on the viewmodel
//    @Test
//    fun `destroys presenter lifecycleowner after using presenter provider`() {
//        val controller = Robolectric.buildActivity(FragmentActivity::class.java).setup() ?
//        val presenter = spy(activity.presenterProvider { (FakePresenter()) }.value)
//        presenter.bind(viewDelegate)
//        lifecycle.currentState = Lifecycle.State.DESTROYED
//        verify(presenter).onPresenterDestroyed()
//        assertNull(presenter.presenterLifecycleOwner)
//    }

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
    fun `presenter injects save state handle`() {
        val presenter by activity.presenterProvider { FakePresenter() }
        assertNotNull(presenter.savedStateHandle)
    }

    @Test
    fun `presenter binds to simple view delegate interface implementation`() {
        val viewDelegateImpl = spy(object: ViewDelegate<ViewState, ViewEvent> {
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
        : BasePresenter<ViewState, ViewEvent>(), SharedImportantFields {
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

    class FakeParentPresenter : BasePresenter<ViewState, ViewEvent>() {
        override fun loadInitialState() {
        }

        lateinit var childPresenter: BasePresenter<ViewState, ViewEvent>

        fun propagate() {
            childPresenter.propagateStatesTo(::onStatePropagated)
        }

        fun onStatePropagated(@Suppress("UNUSED_PARAMETER") state: ViewState) {}
    }

    class FakeViewDelegate(lifecycleOwner: LifecycleOwner, rootView: View) :
        BaseViewDelegate<ViewState, ViewEvent>(lifecycleOwner, rootView) {
        override fun renderViewState(state: ViewState) {}
    }
}