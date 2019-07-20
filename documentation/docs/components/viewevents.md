# ViewEvent
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
