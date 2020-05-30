# Changelog - Amalia

## Amalia 0.7.2

_2020-05-30_

##### Library

!!! bug "Crash with Navigation Fragment Components"
    `Caused by: java.lang.ClassNotFoundException: Didn't find class androidx.lifecycle.AbstractSavedStateVMFactory`

    This occured when including navigation fragment components.
    The problem is due to multiple versions of the savedState library. At some point the naming convention changed.
    To fix we update to saved state 2.2.0+ where the class is consistently `AbstractSavedStateViewModelFactory`

!!! info "Coroutine scope package change"
    Changed package of scopes to `com.vicidroid.amalia.coroutine_scopes`

    This fixes a clash with viewmodel libraries that contain the same class name `CloseableCoroutineScope`

##### Dependencies

!!! example "Dependency updates"
    Added `androidx.fragment:fragment:1.2.4` (ensures Fragment implements interface `SavedStateRegistryOwner`)

    Updated: `androidx.core:core-ktx:1.3.0`

    Updated: `androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0`

    Updated: `androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0`

    Updated: `androidx.lifecycle:lifecycle-livedata-ktx:2.2.0`
___
## Amalia 0.7.1

_2020-05-29_

##### Library changes:

* Added `Main.immediate` coroutine scope extension to presenters

##### Dependency updates:

!!! note ""
    Update: Gradle wrapper to gradle-6.5-milestone-1-all.zip

    Update: Gradle plugin to 4.1.0-alpha10

___
## Amalia 0.7.0

_2020-05-28_

##### Library changes:

* Replace JavaPresenterProvider with variant that requires casting and update documentation for possible issues
* Remove @Parcelable annotation on ListViewState - can cause crash depending on item passed in
___
## Amalia 0.6.9

_2020-04-29_

##### Library changes:

* Rename RecyclerViewEvent to AmaliaCommonEvent
___
## Amalia 0.6.8

_2020-04-29_

##### Library changes:

* Fix index out of bounds error on recycler view (https://github.com/vicidroiddev/amalia/issues/26)
___
## Amalia 0.6.7

_2020-04-09_

##### Library changes:

* Remove state and event type signature from ListViewDelegate
___
## Amalia 0.6.1

_2020-04-08_

##### Library changes:

* Fixed crash when leveraging PresenterProvider for multiple presenters from Java
* Ensure presenter tells view delegate it has has been bound via onBindViewDelegate()
* Reduced restriction on ViewState for RecyclerViewDelegate
* Allow tracking of recycler view items via event propagation
* Remove type signatures from presenters and view delegates to allow for better generic reuse

##### Dependency updates:

* See commit for details:
    - gradle plugin, gradle wrapper
    - junit, kotlin, lifecycle
    - appcompat, corektx, constraintlayout, recyclerview, savedstate
    - mockito, roboelectric, coroutines
    - material, retrofit, moshi, glide, leak canary
___
## Amalia 0.4.1

_2019-10-18_

##### Library changes:

* ViewDelegates can now intercept view events to easily apply lateinit fields residing in some BaseEvent
___
## Amalia 0.4.0

_2019-10-02_

##### New module:

* RecyclerView support
    * Can support numerous presenters that rely on different recycler items

##### Library changes:

* Add feature logging capability for presenters and recyclerviews, see Logging.kt
* Allow child presenter provider to leverage hooks for applying common fields to base presenters
* View delegates now post onViewAttached() on next event loop
* bindViewLifecycleOwner() has been renamed to regular bind(). There are now 3 bind methods to choose from, check documentation for more info.
* presenterLifeCycleOwner field has been removed
* onPresenterDestroyed() was previously not invoked on child presenters
* onCleared has been made final, rely on #onPresenterDestroyed instead

##### Dependency updates:

* Updated Coroutine library to 1.3.0 for scope support
* Updated Kotlin to 1.3.50
* Updated Build tools to 29.0.2
___
## Amalia 0.3.7

_2019-08-14_

##### Library changes:

* Fix dependency issues with samples
___
## Amalia 0.3.6

_2019-07-06_

##### Library changes:

* Name space for library changed after adding second gradle module, docs reflect proper names.
___
## Amalia 0.3.5

_2019-07-06_

##### Library changes:

* Ensure coroutine scopes is present as separate android artifact
___
## Amalia 0.3.4

_2019-06-26_

##### Library changes:

* More useful bind method provided which takes a lifecycleOwner and a lambda for states
* Add coroutine scopes as extension property to presenters (https://github.com/vicidroiddev/amalia/issues/12)
___
## Amalia 0.3.3

_2019-06-15_

##### Library changes:

* Hooks for presenter should be initialized before loadInitialState (https://github.com/vicidroiddev/amalia/issues/17)
___
## Amalia 0.3.2

_2019-06-11_

##### Library changes:

* Remove unnecessary argument for lifecycleowner when observing events (https://github.com/vicidroiddev/amalia/commit/975f5254826d49c6826a71fb279f837740592c7f)
___
## Amalia 0.3.1

_2019-06-10_

##### Library changes:

* Remove live data from ViewDelegate interface (https://github.com/vicidroiddev/amalia/issues/15)
___
## Amalia 0.3.0

_2019-06-09_

Note: This version will rely on android x saved state library to be included in your app.

##### Library changes:

* Add automatic handling of savedInstanceState to amalia components (https://github.com/vicidroiddev/amalia/issues/2)
* Improve overrides in presenter for save state handling (https://github.com/vicidroiddev/amalia/issues/11)
* Abstract ViewDelegate functionality to an interface (https://github.com/vicidroiddev/amalia/issues/13)

##### Dependency updates:

* com.android.tools.build:gradle:3.5.0-beta05
* androidx.constraintlayout:constraintlayout:2.0.0-beta2
* androidx.core:core-ktx:1.2.0-alpha02'
* androidx.test.espresso:espresso-core:3.3.0-alpha01
* androidx.test:runner:1.3.0-alpha01
___
## Amalia 0.2.0

_2019-06-04_

* Add lifecycle methods to view delegate (https://github.com/vicidroiddev/amalia/issues/4)
* Enhance the concept of binding to a presenter (https://github.com/vicidroiddev/amalia/issues/5)
* Pushstate should resolve the correct thread (https://github.com/vicidroiddev/amalia/issues/6)
* Abstract ViewDelegate functionality to an interface (https://github.com/vicidroiddev/amalia/issues/13)