# Amalia

Amalia is a painless Android MVP implementation which facilitates separation of concerns and dictates a straightforward uni-directional flow of data between view delegator and presenter.

Amalia leverages Android's architecture components to ensure compliance with the lifecycle. Furthermore, Amalia's presenters are backed by Android Jetpack's ViewModel to ensure seemless restoration during configuration changes.

## Components
#####  Presenters:
- Should perform all data loading/saving/refreshing (anything background heavy)
- Can have member fields that represent the current data (these will survive configuration changes)
- Will survive configuration changes (member fields stay in tact)
- Should leverage `pushState(...)` to expose a meaningful `ViewState`.
- Should inherit from `BasePresenter<ViewState, ViewEvent>`
- Should never contain views, or activity contexts that may leak
- Can leverage the application context field to get resources

#####  ViewDelegates:
- Should perform all view related and be fairly dumb (no business logic)
- Should inherit from `BaseViewDelegate<ViewState, ViewEvent>`
- Should override `renderViewState(...)` to process incoming ViewStates
- Can contain contexts, views or anything else that you would normally put in an Activity/Fragment
- Should contain your `findViewById` calls
- Should contain your various view listeners (`onClick`, `onCheckChanged` etc..)
- Should leverage `pushEvent(...)` to expose a meaningful `ViewEvent`
- Should not contain business logic

#####  ViewState:

Create your own ViewState by inheriting from the `ViewState` interface.

    sealed class MyViewState : ViewState {
      class MyItemsLoaded(val data: List<String>) : MyViewState()
	  class NoItemsFound : MyViewState()
    }

In your presenter, after performing database queries in the background call

    // Only if you are in the main thread
	pushState(MyViewState.MyItemsLoaded(data))
	// If still in a background thread
	pushStateOnMainLooper(MyViewState.MyItemsLoaded(data))


## To get started

In your root level build.gradle file:

```gradle
allprojects {
  repositories {
    mavenCentral()
    google()
    jcenter()
  }
}
```

In your app level build.gradle file:

```gradle
dependencies {
  implementation 'com.github.vicidroiddev:amalia:latest_version'
}
```

The latest version can be seen on maven: 
https://search.maven.org/search?q=g:com.github.vicidroiddev%20AND%20a:amalia&core=gav


## Examples

Code samples coming soon!
