import buildSrc.PublishHelper.ensurePomDetails
import buildSrc.PublishHelper.useLocalBuildMavenRepo
import buildSrc.AndroidVersion
import buildSrc.ReleaseInfo
import buildSrc.DepVersions
import buildSrc.LintSettings
import buildSrc.Deps
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

//group = "com.github.vicidroiddev"

android {
    compileSdk = AndroidVersion.compileSdk

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
//            "com.vicidroid.amalia",
//            "-Xexplicit-api=strict",
//            "-Xopt-in=kotlin.RequiresOptIn"
//        )
    }

    kotlinOptions {
        languageVersion = DepVersions.kotlinLanguageVersion
        jvmTarget = DepVersions.jvmTarget
    }

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

    namespace = "com.vicidroid.amalia.ui.recyclerview"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Deps.kotlinStdlib)

    implementation(Deps.appcompat)
    implementation(Deps.annotation)
    implementation(Deps.recyclerview)
    implementation(project(":amalia-core"))

    // We must expose sticky headers because we expose the adapter
    api(Deps.stickyheaders)
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