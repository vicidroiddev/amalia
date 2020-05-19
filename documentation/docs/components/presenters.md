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

## How do I use multiple presenters in Java?

!!! warning
    Java does not support Kotlin reified types. As such the following signature cannot be used reliabily from Java:

    `fun <reified P : BasePresenter> Fragment.presenterProvider(.....)`

    While it is possible to write a wrapper, a casting error will occur if more than one presenter is invoked in a given java class.

To get around this problem you could convert your class to Kotlin to make use of reified types.
However, if conversion is not desired, it is possible to specify the reified types in a wrapper function that returns an exact type.

Create a Kotlin file with one or many package level functions

```kotlin
// MyProviders.kt
fun provideFeature1Presenter(fragment: Fragment) : Feature1Presenter  =
    fragment.presenterProviderExt { provideFeature1Presenter() }.value

fun provideFeature2Presenter(fragment: Fragment) : Feature2Presenter =
    fragment.presenterProviderExt { provideFeature2Presenter() }.value
```

Use those package functions from your java fragment

```kotlin
public class MyFragment extends Fragment
    private Feature1Presenter mFeature1Presenter;
    private Feature2Presenter mFeature2Presenter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeature1Presenter = provideFeature1Presenter(this);
        mFeature2Presenter = provideFeature2Presenter(this);
    }

    // bind the presenters in onViewCreated
```