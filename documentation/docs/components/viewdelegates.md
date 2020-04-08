# ViewDelegates

-   Should perform all view related tasks and be fairly dumb (no business logic)
-   Should inherit from  `BaseViewDelegate`
-   Should override  `renderViewState(...)`  to process incoming ViewStates
-   Can contain contexts, views or anything else that you would normally put in an Activity/Fragment
-   Should contain your  `findViewById`  calls
-   Should contain your various view listeners (`onClick`,  `onCheckChanged`  etc..)
-   Should leverage  `pushEvent(...)`  to expose a meaningful  `ViewEvent`


## How do I apply important fields to all events?

You likely have a base class for your own `MyAppBaseViewDelegate`.
In there you can override `onInterceptEventChain()`.


```kotlin
// Create your base view event. All of your view events should extend from this event
open class BaseDefaultViewEvent : ViewEvent {
    lateinit var importantSharedField: String
}

// Create your own variant of a BaseMyAppViewDelegate
// Note that the event extends from `BaseDefaultViewEvent`
abstract class MyAppBaseViewDelegate(
    view: View,
    lifecycleOwner: LifecycleOwner
) : BaseViewDelegate(lifecycleOwner, view) {

    // Override this method and inject any fields you defined
    override fun onInterceptEventChain(event: ViewEvent) {
        event.importantSharedField = provideImportantField()
    }

    abstract fun provideImportantField() : String
}
```