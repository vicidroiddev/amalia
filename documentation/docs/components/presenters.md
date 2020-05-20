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

## How do I use presenter provider in Java?

!!! warning
    Java does not support Kotlin reified types. As such the following signature cannot be used reliabily from Java:

    `fun <reified P : BasePresenter> Fragment.presenterProvider(.....)`

    While it is possible to write a wrapper, ensure you do not retain the generic type `<T>`.
    A casting error will occur if more than one presenter is invoked in a given java class.
    The generic type `<T>` will be translated to the first presenter invoked due to the type erasure problem in Java.

    The optimal solution is to convert your Java code to Kotlin. The alternative will involve casting or creating wrappers.

### Alernatives:

1. Leverage `JavaPresenterProvider` and cast your presenters to the appropriate type.

```kotlin
public class MyFragment extends Fragment
    private Feature1Presenter mFeature1Presenter;
    private Feature2Presenter mFeature2Presenter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeature1Presenter = ((Feature1Presenter) JavaPresenterProvider.provide(this, Feature1Presenter::new));
        mFeature2Presenter = ((Feature2Presenter) JavaPresenterProvider.provide(this, Feature2Presenter::new));
    }

    // bind the presenters in onViewCreated
}

```

2. Use package functions which specify the type.

```kotlin
// MyProviders.kt
fun provideFeature1Presenter(fragment: Fragment) : Feature1Presenter  =
    fragment.presenterProviderExt { provideFeature1Presenter() }.value

fun provideFeature2Presenter(fragment: Fragment) : Feature2Presenter =
    fragment.presenterProviderExt { provideFeature2Presenter() }.value
```



```kotlin
// Use those package functions from your java fragment
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
}
```