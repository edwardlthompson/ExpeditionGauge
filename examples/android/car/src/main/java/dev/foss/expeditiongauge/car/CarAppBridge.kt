package dev.foss.expeditiongauge.car

/** In-process bridge from phone app to car UI (same APK). */
interface CarAppBridge {
    fun isAndroidAutoEnabled(): Boolean
    fun allowedMetricKeys(): Set<String>
    fun metricValues(): Map<String, String>
    fun isRecording(): Boolean
    fun startRecording(): Boolean
    fun stopRecording(): Boolean
    fun markEvent(): Boolean
}

object CarAppBridgeRegistry {
    @Volatile
    var bridge: CarAppBridge? = null
}
