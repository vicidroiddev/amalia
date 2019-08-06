#Amalia [![Build Status](https://app.bitrise.io/app/75917df26e15facf/status.svg?token=D9tM0WbyOEdD_LmUP1g5ZA&branch=master)](https://app.bitrise.io/app/75917df26e15facf) [![Jitpack](https://jitpack.io/v/vicidroiddev/amalia.svg)](https://jitpack.io/#vicidroiddev/amalia)

##What is Amalia?

Amalia is an MVP/MVI implementation dictating a straightforward uni-directional flow of view states to render and view events to process.


##Features
- **100% Kotlin** with pleasant apis that can still be consumed from Java
- **Coroutine scope cancellation support** via optional gradle dependency
- **Decouple the UI** from business logic but allow communication between the two
- **Lifecycle-aware presenters** which automatically clean up
- **Modern Android Jetpack components**
	 - presenters survive configuration changes via Jetpack's `ViewModelStore`
	 - presenters can receive lifecycle callbacks via a `LifecycleOwner`
	 - presenters are loosely coupled to views via `LiveData` observers
	 - presenters can recover from process death with the help of `SavedStateRegistryOwner`
- **Structure legacy code** without refactoring every single piece, even from Java.
- **Prevent common crashes** due to running code when view is not ready or destroyed.

