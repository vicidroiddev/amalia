package buildSrc

import org.gradle.api.JavaVersion

object ReleaseInfo {
    const val libraryGroupId = "com.vicidroid.amalia"

    const val ARTIFACT_REPO_GITHUB = "https://maven.pkg.github.com/vicidroiddev/amalia"

    /**
     * MAJOR.MINOR.PATCH
     * - MAJOR when you make incompatible API changes,
     * - MINOR when you add functionality in a backwards-compatible manner
     * - PATCH when you make backwards-compatible bug fixes.
     */
    const val libraryVersion: String = "0.9.1"

    // Need to encapsulate in extra quotes for BuildConfig values
    fun libraryVersionStr() = "\"${libraryVersion}\""
}

object LintSettings {
    const val warnOnErrors = false
    const val abortOnErrors = false
}

object DepVersions {
    const val kotlinLanguageVersion = "1.7"
    const val kotlin = "1.7.20"
    const val jvmTarget = "1.8"
    val javaCompatibility = JavaVersion.VERSION_11

    const val junit = "4.13.2"
    const val lifecycle = "2.5.0"
}

object AndroidVersion {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33
}

object Deps {
    // Android X
    const val appcompat = "androidx.appcompat:appcompat:1.5.1"
    const val annotation = "androidx.annotation:annotation:1.5.0"
    const val coreKtx = "androidx.core:core-ktx:1.9.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.2.0-alpha05"
    const val legacySupport = "androidx.legacy:legacy-support-v4:1.0.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
    const val fragment = "androidx.fragment:fragment:1.5.5"

    // Architecture Components
    const val archCoreTesting = "android.arch.core:core-testing:1.1.1"
    const val commonJava8 = "android.arch.lifecycle:common-java8:1.1.1"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${DepVersions.lifecycle}"
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${DepVersions.lifecycle}"
    const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${DepVersions.lifecycle}"

    // Toolchain
    const val jitpackMavenPlugin = "com.github.dcendents:android-maven-gradle-plugin:2.1"

    // Dependencies
    const val junit = "junit:junit:${DepVersions.junit}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${DepVersions.kotlin}"
//    const val mjaarmanMockito = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    const val mjaarmanMockito = "org.mockito.kotlin:mockito-kotlin:4.1.0"
    const val roboelectric = "org.robolectric:robolectric:4.9"
    const val stickyheaders = "com.github.vicidroiddev:sticky-headers-recyclerview:0.4.3-EAP-02@aar"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6."
}


fun createStringArrayValue(value: Set<String>): String {
    // Create String that holds Java String Array code
    return value.joinToString(prefix = "{", separator = ",", postfix = "}", transform = { "\"$it\"" })
}