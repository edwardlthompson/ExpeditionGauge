package dev.foss.expeditiongauge.car

/** In-process bridge from phone app to car UI (same APK). */
interface CarAppBridge {
    fun isAndroidAutoEnabled(): Boolean
    /** [displaySpec] comes from CarContext only — never phone Display.rotation. */
    fun hudTiles(displaySpec: AaDisplaySpec = AaDisplaySpec.DEFAULT): CarHudTiles
    fun metricValues(): Map<String, String>
    fun isRecording(): Boolean
    fun startRecording(): Boolean
    fun stopRecording(): Boolean
    fun markEvent(): Boolean
    fun zeroAttitude(): Boolean
    fun setInvalidationListener(listener: (() -> Unit)?)
}

object CarAppBridgeRegistry {
    @Volatile
    var bridge: CarAppBridge? = null
}
