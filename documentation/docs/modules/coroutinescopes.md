# Coroutine Scopes

All provided coroutine scope extensions are cancelled when the presenter goes through destruction, dictated by the `presenterProvider`.

#####Available scopes in a presenter extending from `BasePresenter`

```kotlin
mainScope.launch { //uses the coroutine Main dispatcher, preferred scope for presenters } 

ioScope.launch { //uses the coroutine IO dispatcher } 

defaultScope.launch { //uses the default coroutine dispatcher } 
```

##Adding gradle dependency
Add the amalia dependency in your app level build.gradle file.

```groovy
implementation 'com.github.vicidroiddev.amalia:amalia-coroutine-scopes:{latest_version}@aar'
```
[![Jitpack](https://jitpack.io/v/vicidroiddev/amalia.svg)](https://jitpack.io/#vicidroiddev/amalia)

##Example usage:

```kotlin
class DiscoverPresenter(private val repository: DiscoverRepository) :
    BasePresenter() {

    override fun loadInitialState() {
        // mainScope is provided via amalia-coroutine-scopes dependency
        mainScope.launch {
            // discoverFromApi is a suspending function which runs on the io dispatcher
            // if it takes a long time and the view is closed, we will automatically call cancel() on the scope.
            val results = repository.discoverFromApi()
            
            //Okay main thread here, lets push the view state
            pushState(DiscoverState.Loaded(results))
        }
    }
}
```