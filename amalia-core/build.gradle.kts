import buildSrc.PublishHelper.ensurePomDetails
import buildSrc.AndroidVersion
import buildSrc.ReleaseInfo
import buildSrc.DepVersions
import buildSrc.LintSettings
import buildSrc.Deps
import buildSrc.PublishHelper.useLocalBuildMavenRepo
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

group = "com.github.vicidroiddev"

android {
    compileSdk = AndroidVersion.compileSdk

    packagingOptions {
        resources.excludes.add("META-INF/**/*")
    }

    defaultConfig {
        minSdk = AndroidVersion.minSdk
        targetSdk = AndroidVersion.targetSdk
        version = ReleaseInfo.libraryVersion

        aarMetadata {
            minCompileSdk = AndroidVersion.compileSdk
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "AMALIA_LIBRARY_VERSION", ReleaseInfo.libraryVersionStr())
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
            proguardFile("proguard-rules.pro")
//            proguardFile("proguard-rules-logging.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
        }
    }


    // Target Java 8 to use androidx.lifecycle.DefaultLifecycleObserver
    compileOptions {
        sourceCompatibility = DepVersions.javaCompatibility
        targetCompatibility = DepVersions.javaCompatibility

        //TODO enable this to ensure that visibility is set on all fields for explicit api mode.
//        kotlinOptions.freeCompilerArgs += listOf(
//            "-module-name",
//            "ai.literal.library",
//            "-Xexplicit-api=strict",
//            "-Xopt-in=kotlin.RequiresOptIn"
//        )
    }

    kotlinOptions {
        languageVersion = DepVersions.kotlinLanguageVersion
        jvmTarget = DepVersions.jvmTarget
    }

//    sourceSets {
//        main.java.srcDirs += "src/main/kotlin"
//    }

    lint {
        // Changle gradle version to find relevant lint docs
        // https://developer.android.com/reference/tools/gradle-api/7.1/com/android/build/api/dsl/Lint
        warningsAsErrors = LintSettings.warnOnErrors
        ignoreWarnings = LintSettings.abortOnErrors
        // We don't want to impose RTL on consuming applications.
        disable += "RtlEnabled"
    }

    sourceSets {
        getByName("test") {
            java.srcDir("assets")
        }
        getByName("androidTest") {
            java.srcDir("assets")
        }
    }

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }

    namespace = "com.vicidroid.amalia"
}

dependencies {
    implementation(Deps.kotlinStdlib)

    implementation(Deps.coroutines)

    // To support persistence of a presenter after config changes
    implementation(Deps.viewmodel)

    // To support sending and receiving of events and states on the UI thread
    implementation(Deps.livedata)

    // To support DefaultLifecycleObserver, api because presenters are exposed
    implementation(Deps.commonJava8)

    // Extensions on Activities
    implementation(Deps.appcompat)
    implementation(Deps.annotation)

    // Saved state support in presenters, api because saveStateHandle is exposed
    api(Deps.savedState)

    // To access saved state registry owner
    implementation(Deps.fragment)

    testImplementation(Deps.junit)
    testImplementation(Deps.fragment)
    testImplementation(Deps.mjaarmanMockito)
    testImplementation(Deps.roboelectric)
    testImplementation(Deps.archCoreTesting)
}


publishing {
    publications {
        create<MavenPublication>("release") {
            ensurePomDetails(project.name)
            groupId = ReleaseInfo.libraryGroupId
            artifactId = project.name
            version = ReleaseInfo.libraryVersion
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        useLocalBuildMavenRepo(uri("${project.parent!!.buildDir}/repo"))

        maven {
            name = "GithubPackages"
            url = uri(ReleaseInfo.ARTIFACT_REPO_GITHUB)
            credentials {
//                System.out.println(gradleLocalProperties(rootDir).getProperty("gpr.user"))
//                System.out.println(gradleLocalProperties(rootDir).getProperty("gpr.token"))
                // Stored props are in local.properties
                username =
                    gradleLocalProperties(rootDir).getProperty("gpr.user") ?: System.getenv(
                        "ARTIFACT_USER"
                    )
                password = gradleLocalProperties(rootDir).getProperty("gpr.token")
                    ?: System.getenv("ARTIFACT_TOKEN")
            }
        }
    }
}