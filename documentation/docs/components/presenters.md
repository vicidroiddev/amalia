# Presenters

- Should perform all data loading/saving/refreshing (anything background heavy)
- Will survive configuration changes (member fields stay in tact)
- Should leverage  `pushState(...)`  to expose a meaningful  `ViewState`.
- Should inherit from  `BasePresenter`
- Should override  `onViewEvent(...)`  to process incoming ViewEvents
- Should never contain views, or activity contexts that may leak
- Can leverage the application context field to get resources
- Can use savedStateHandle for process death restoration

## Is there a callback for when my presenter is ready to go?
```kotlin
class MyPresenter : BasePresenter() {
    override fun loadInitialState() {
        // Can access applicationContext at this point
        applicationContext.getString(R.string.somestring)
    }
}
```

## Is there a callback for when my presenter is destroyed?
`onPresenterDestroyed()` will be invoked when the presenter is about to die. This will also be called for child presenters.
This method is not invoked on configuration changes.

```kotlin
class MyPresenter : BasePresenter() {
    override fun onPresenterDestroyed() {
        // Clean up time
        // Even better track your closeable objects, see below
    }
}
```

## What's a good way to clean up long running operations?
You may store your object in the presenter cache if it implements `Closeable`.
That way you can be sure it is automatically cleaned up at the right time.

Note: Amalia uses this mechanism for `amalia-coroutine-scopes` module.


```kotlin
class MyPresenter : BasePresenter() {
    init {
        val longRunningThing = LongRunningThing()
        // Objects will automatically have close() called when the presenter is destroyed.
        closeableObjects.put("my_key", longRunningThing)
    }

    // Ensure we implement Closeable and cancel any operations needed.
    class LongRunningThing : Closeable {
        override fun close() {
            // Stop long running thing
        }
    }
}
```