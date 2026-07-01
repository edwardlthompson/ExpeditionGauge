# ExpeditionGauge release shrink rules (FOSS sideload APK)

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose / Kotlin
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable <methods>;
}

# MapLibre / OkHttp (reflection-safe keeps)
-dontwarn org.maplibre.**
-dontwarn okhttp3.internal.platform.**
-keep class org.maplibre.** { *; }

# Car App Library
-keep class androidx.car.app.** { *; }

# ZXing
-keep class com.google.zxing.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
