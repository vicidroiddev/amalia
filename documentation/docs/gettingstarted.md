##Adding gradle dependency

Amalia is available via [Jitpack](https://jitpack.io/#vicidroiddev/amalia)

Include the `jitpack` maven repository in your root level build.gradle file.
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Now add the amalia dependency in your app level build.gradle file.

```groovy
implementation ("com.github.vicidroiddev.amalia:amalia-core:{latest_version}@aar") {
    transitive = true
}
```

[![Jitpack](https://jitpack.io/v/vicidroiddev/amalia.svg)](https://jitpack.io/#vicidroiddev/amalia)

##1. Define states and events

You can define states representing a snapshot in time of what the view should render. Sealed classes are perfect for this!

You can also define the UI events that can be propagated to the presenter for further processing.

In short:

- Presenter pushes state -> ViewDelegate renders the state
- ViewDelegate pushes event -> Presenter processes the event

```kotlin
sealed class DiscoverState : ViewState {
    data class Loaded(val data: DiscoverTvResult) : DiscoverState()
}


sealed class DiscoverEvent : ViewEvent {
    object MarkShowAsFavourite : DiscoverEvent()
}
```

##2. Define presenter

Extend from the `BasePresenter` class and load all your data here. When you are finished loading, push an appropriate state.
Before loading, you can push a state to represent _loading_.
This can be rendered in the view event as a progress dialog or an empty state view.

```kotlin
class DiscoverTvPresenter() :
    BasePresenter<DiscoverState, DiscoverEvent>() {

    private val repository = DiscoverTvRepository()

    override fun loadInitialState() {
        val results = repository.getDiscoverTvResults()
        pushState(DiscoverState.Loaded(results))
    }

    override fun onBindViewDelegate(viewDelegate: ViewDelegate<DiscoverState, DiscoverEvent>) {
        Log.v("DiscoverPresenter", "View was bound to presenter")
    }

    override fun onViewEvent(event: DiscoverEvent) {
        when (event) {
            is DiscoverEvent.MarkShowAsFavourite -> {
                repository.saveFavouriteShow(event.show)
            }
        }
    }
}
```

##3. Define view delegate

Extend from the `BaseViewDelegate` class or some other derivative if lists are required. Override `renderViewState` and apply UI changes as needed.

Apply your onclick listeners and push the appropriate event to your presenter for processing.


```kotlin

class DiscoverTvViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<DiscoverTvState, DiscoverTvEvent>(lifecycleOwner, view) {

    val tvShow: TextView = view.findViewById(R.id.discover_tv_name)
    val lastAiredDate: TextView = view.findViewById(R.id.discover_tv_last_aired)
    val favouriteBtn: Button = view.findViewById(R.id.discover_tv_fav_btn)

    init {
        favouriteBtn.setOnClickListener { pushEvent(DiscoverTvEvent.MarkShowAsFavourite)}
    }

    override fun renderViewState(state: DiscoverTvState) {
        when (state) {
            is DiscoverState.Loaded -> {
                tvShow.setText(state.show.name)
                lastAiredDate.setText(state.show.lastAiredDate)
            }
        }
    }
}
```

##4. Bind the presenter and delegate

Bind the presenter and the view delegate together. This can be done in an activity, fragment, or even a custom view.

Leverage the `presenterProvider` to ensure your presenter is lifecycle aware and retained across configuration changes.

```kotlin
class DiscoverTvActivity : BaseActivity() {
    private val discoverTvPresenter by presenterProvider {
        DiscoverTvPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        discoverTvPresenter.bind(DiscoverTvViewDelegate(this, window.decorView.rootView))
    }
}

```



