plugins {
    id("com.android.library")
}

android {
    namespace = "dev.foss.expeditiongauge.car"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Opt-in AA glance PNG export: -PaaPreviewDir=path (see aa-bitmap-preview.ps1)
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperty(
                    "aa.preview.dir",
                    (project.findProperty("aaPreviewDir") as String?) ?: "",
                )
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api("androidx.car.app:app:1.7.0")
    implementation("androidx.core:core:1.19.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16.1")
}
