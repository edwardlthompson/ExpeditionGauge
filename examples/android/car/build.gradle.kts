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
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.car.app:app:1.4.0")

    testImplementation("junit:junit:4.13.2")
}
