# ViewEvent
Create your own ViewEvent by implementing the  `ViewEvent`  interface.

```kotlin
sealed class ProfileViewEvent : ViewEvent {
  class SaveProfile(val profile: Profile) : ProfileViewEvent()
  object CancelEditProfile() : ProfileViewEvent()
}
```

In your view delegate, in any appropriate listener you could fire an event:

```kotlin
// Can only be called from the main thread.
pushEvent(ProfileViewEvent.SaveProfile(profile))
```

In your presenter, override `onViewEvent(event: E)` to receive the `SaveProfile` event that was just sent.
