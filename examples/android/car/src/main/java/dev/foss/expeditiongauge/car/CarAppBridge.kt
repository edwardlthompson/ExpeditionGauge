package dev.foss.expeditiongauge.car

/** In-process bridge from phone app to car UI (same APK). */
interface CarAppBridge {
    fun isAndroidAutoEnabled(): Boolean
    /** [displaySpec] comes from CarContext only — never phone Display.rotation. */
    fun hudTiles(displaySpec: AaDisplaySpec = AaDisplaySpec.DEFAULT): CarHudTiles
    fun metricValues(): Map<String, String>
    fun isRecording(): Boolean
    /** Returns true if the request was accepted (or already recording). Never throws. */
    fun startRecording(): Boolean
    /** Returns true if the request was accepted (or already stopped). Never throws. */
    fun stopRecording(): Boolean
    fun markEvent(): Boolean
    /**
     * Returns false immediately if Zero is unavailable (e.g. non-phone fusion).
     * Otherwise accepts and runs async. Never throws.
     */
    fun zeroAttitude(): Boolean
    fun setInvalidationListener(listener: (() -> Unit)?)
    /** Optional HU toast for async failures (storage full, etc.). */
    fun setToastHandler(handler: ((String) -> Unit)?)
    /** Keep IMU/GPS/BLE alive while the car session is connected. */
    fun onCarSessionStarted()
    fun onCarSessionStopped()
}

object CarAppBridgeRegistry {
    @Volatile
    var bridge: CarAppBridge? = null
}
