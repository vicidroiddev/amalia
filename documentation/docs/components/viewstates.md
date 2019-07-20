# ViewState
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