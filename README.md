# Amalia

Amalia is a painless Android MVP implementation which facilitates separation of concerns and dictates a straightforward uni-directional flow of data between components.

Amalia leverages Android's architecture components to ensure compliance with the lifecycle. Furthermore, Amalia's presenters are backed by ViewModelProvider to ensure seemless restoration during configuration changes.

## To get started

In your root level build.gradle file:

```gradle
allprojects {
  repositories {
    google()
    jcenter()
    maven { url 'https://github.com/vicidroiddev/amalia/raw/maven_repo/NOT_YET_READY'}
  }
}
```

In your app level build.gradle file:

```gradle
dependencies {
  implementation 'implementation 'com.github.vicidroiddev:amalia:X.Y.ZNOT_YET_READY'
}
```

## Components

TODO: explain components

## Examples

TODO: example Fragment with view lifecycle owner
