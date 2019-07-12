# Amalia [![Build Status](https://app.bitrise.io/app/75917df26e15facf/status.svg?token=D9tM0WbyOEdD_LmUP1g5ZA&branch=master)](https://app.bitrise.io/app/75917df26e15facf)
### What is this library and why bother?
- Amalia is an MVP implementation which dictates a straightforward pattern of uni-directional flow of view states to render and view events to process.
- 100% written in Kotlin with pleasant apis that can still be consumed from Java when needed. 
- Provides lifecycle-aware presenters which automatically clean up leakable contexts
- Leverages modern Android Jetpack components:
	 - presenters survive configuration changes via Jetpack's `ViewModelStore`
	 - presenters can receive lifecycle callbacks via a `LifecycleOwner`
	 - presenters are loosely coupled to views via `LiveData` observers
	 - presenters can recover from process death with the help of `SavedStateRegistryOwner`
- Can be leveraged within existing legacy code, without refactoring all of your code, even from Java.
- Prevents common crashes due to running code when view is not ready or destroyed.

## Components

#### Presenters:

- Should perform all data loading/saving/refreshing (anything background heavy)
- Will survive configuration changes (member fields stay in tact)
- Should leverage  `pushState(...)`  to expose a meaningful  `ViewState`.
- Should inherit from  `BasePresenter<ViewState, ViewEvent>`
- Should override  `onViewEvent(...)`  to process incoming ViewEvents
- Should never contain views, or activity contexts that may leak
- Can leverage the application context field to get resources
- Can use savedStateHandle for process death restoration

#### ViewDelegates:

-   Should perform all view related tasks and be fairly dumb (no business logic)
-   Should inherit from  `BaseViewDelegate<ViewState, ViewEvent>`
-   Should override  `renderViewState(...)`  to process incoming ViewStates
-   Can contain contexts, views or anything else that you would normally put in an Activity/Fragment
-   Should contain your  `findViewById`  calls
-   Should contain your various view listeners (`onClick`,  `onCheckChanged`  etc..)
-   Should leverage  `pushEvent(...)`  to expose a meaningful  `ViewEvent`

#### ViewState:
Create your own ViewState by implementing the  `ViewState`  interface.

```
sealed class ProfileViewState : ViewState {
  class ProfileLoaded(val profile: Profile) : ProfileViewState()
  object NoProfileFound : ProfileViewState()
}

```

In your presenter, after performing database queries in the background call

```
// Can be called from a background thread
pushState(MyViewState.ProfileLoaded(profile))
```
In your view delegate, override `renderViewState(state: S)` to receive the `ProfileLoaded` state that was just sent. 


#### ViewEvent:
Create your own ViewEvent by implementing the  `ViewEvent`  interface.

```
sealed class ProfileViewEvent : ViewEvent {
  class SaveProfile(val profile: Profile) : ProfileViewEvent()
  object CancelEditProfile() : ProfileViewEvent()
}

```

In your view delegate, in any appropriate listener you could fire an event

```
// Can only be called from the main tread.
pushEvent(ProfileViewEvent.SaveProfile(profile))
```
In your presenter, override `onViewEvent(event: E)` to receive the `SaveProfile` event that was just sent. 

## To get started

In your root level build.gradle file:

```
allprojects {
  repositories {
    maven {
        url 'https://jitpack.io'
    }
  }
}

```

In your app level build.gradle file:

```
dependencies {
  implementation 'com.github.vicidroiddev:amalia:latest_version'
}

```
> The latest version can be seen on jitpack: https://jitpack.io/#vicidroiddev/amalia

## Examples

Code samples coming soon!
