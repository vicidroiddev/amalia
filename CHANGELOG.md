#Changelog - Amalia

## Amalia 0.X.X

_2017-XX-XX_

#####Library changes:

* Remove unnecessary argument for lifecycleowner when observing events (https://github.com/vicidroiddev/amalia/commit/975f5254826d49c6826a71fb279f837740592c7f)


## Amalia 0.3.1

_2017-06-10_

#####Library changes:

* Remove live data from ViewDelegate interface (https://github.com/vicidroiddev/amalia/issues/15)


## Amalia 0.3.0

_2017-06-09_

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

_2017-06-04_

* Add lifecycle methods to view delegate (https://github.com/vicidroiddev/amalia/issues/4)
* Enhance the concept of binding to a presenter (https://github.com/vicidroiddev/amalia/issues/5)
* Pushstate should resolve the correct thread (https://github.com/vicidroiddev/amalia/issues/6)
* Abstract ViewDelegate functionality to an interface (https://github.com/vicidroiddev/amalia/issues/13)