package dev.foss.expeditiongauge.car

/** In-process bridge from phone app to car UI (same APK). */
interface CarAppBridge {
    fun isAndroidAutoEnabled(): Boolean
    fun hudTiles(): CarHudTiles
    fun metricValues(): Map<String, String>
    fun isRecording(): Boolean
    fun startRecording(): Boolean
    fun stopRecording(): Boolean
    fun markEvent(): Boolean
    fun setInvalidationListener(listener: (() -> Unit)?)
}

object CarAppBridgeRegistry {
    @Volatile
    var bridge: CarAppBridge? = null
}
