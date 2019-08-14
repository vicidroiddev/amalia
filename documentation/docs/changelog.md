#Changelog - Amalia

## Amalia 0.3.7

_2019-08_14_

#####Library changes:

* Fix dependency issues with samples

## Amalia 0.3.6

_2019-07_06_

#####Library changes:

* Name space for library changed after adding second gradle module, docs reflect proper names.


## Amalia 0.3.5

_2019-07_06_

#####Library changes:

* Ensure coroutine scopes is present as separate android artifact


## Amalia 0.3.4

_2019-06_26_

#####Library changes:

* More useful bind method provided which takes a lifecycleOwner and a lambda for states
* Add coroutine scope to presenter #12 (https://github.com/vicidroiddev/amalia/issues/12)


## Amalia 0.3.3

_2019-06_15_

#####Library changes:

* Hooks for presenter should be initialized before loadInitialState (https://github.com/vicidroiddev/amalia/issues/17)


## Amalia 0.3.2

_2019-06_11_

#####Library changes:

* Remove unnecessary argument for lifecycleowner when observing events (https://github.com/vicidroiddev/amalia/commit/975f5254826d49c6826a71fb279f837740592c7f)


## Amalia 0.3.1

_2019-06-10_

#####Library changes:

* Remove live data from ViewDelegate interface (https://github.com/vicidroiddev/amalia/issues/15)


## Amalia 0.3.0

_2019-06-09_

Note: This version will rely on android x saved state library to be included in your app.

#####Library changes:

* Add automatic handling of savedInstanceState to amalia components (https://github.com/vicidroiddev/amalia/issues/2)
* Improve overrides in presenter for save state handling (https://github.com/vicidroiddev/amalia/issues/11)
* Abstract ViewDelegate functionality to an interface (https://github.com/vicidroiddev/amalia/issues/13)

#####Dependency updates:

* com.android.tools.build:gradle:3.5.0-beta05
* androidx.constraintlayout:constraintlayout:2.0.0-beta2
* androidx.core:core-ktx:1.2.0-alpha02'
* androidx.test.espresso:espresso-core:3.3.0-alpha01
* androidx.test:runner:1.3.0-alpha01

## Amalia 0.2.0

_2019-06-04_

* Add lifecycle methods to view delegate (https://github.com/vicidroiddev/amalia/issues/4)
* Enhance the concept of binding to a presenter (https://github.com/vicidroiddev/amalia/issues/5)
* Pushstate should resolve the correct thread (https://github.com/vicidroiddev/amalia/issues/6)
* Abstract ViewDelegate functionality to an interface (https://github.com/vicidroiddev/amalia/issues/13)