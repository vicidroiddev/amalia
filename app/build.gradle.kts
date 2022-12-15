import buildSrc.*
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

val themoviedbToken = gradleLocalProperties(rootDir).getProperty("api.themoviedb")

android {
    compileSdk = AndroidVersion.compileSdk

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.vicidroid.amalia.sample"
        minSdk = AndroidVersion.minSdk
        targetSdk = AndroidVersion.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "themoviedbToken", themoviedbToken ?: "NOT_THE_REAL_KEY")
    }

    // Target Java 8 to use androidx.lifecycle.DefaultLifecycleObserver
    compileOptions {
        sourceCompatibility = DepVersions.javaCompatibility
        targetCompatibility = DepVersions.javaCompatibility
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }
    }

    namespace = "com.vicidroid.amalia.sample"
}

kapt {
    useBuildCache = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // JETBRAINS
    implementation(Deps.coroutines)
    implementation(Deps.kotlinStdlib)

    // ANDROID X
    implementation(Deps.appcompat)
    implementation(Deps.annotation)
    implementation(Deps.coreKtx)
    implementation(Deps.constraintLayout)
    implementation(Deps.legacySupport)
    implementation(Deps.recyclerview)

    // MATERIAL
    implementation("com.google.android.material:material:1.8.0-beta01")

    // TESTS
    testImplementation(Deps.junit)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    implementation("com.github.bumptech.glide:glide:4.14.2")

    // Adding navigation components
//    implementation 'androidx.navigation:navigation-fragment:2.2.1'
//    implementation 'androidx.navigation:navigation-ui:2.2.1'
//    implementation 'androidx.navigation:navigation-fragment-ktx:2.2.1'
//    implementation 'androidx.navigation:navigation-ui-ktx:2.2.1'

    // AMALIA LIB EXTERNAL
    val amaliaVersion = "0.9.0-EAP-02"

    // aar includes android resource files, jar does not.
    // We should explicitly ask for an aar by using @aar
    // Note: when using @aar syntax, gradle will drop transitive dependencies.
    // We must include transitive dependencies to support the android libraries amalia depends on.
//    implementation ("com.github.vicidroiddev.amalia:amalia-core:$amaliaVersion@aar") {
//        transitive = true
//    }
//    implementation ("com.github.vicidroiddev.amalia:amalia-recyclerview:$amaliaVersion@aar")

    // AMALIA LIB INTERNAL
    implementation(project(":amalia-core"))
    implementation(project(":amalia-recyclerview"))

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
}