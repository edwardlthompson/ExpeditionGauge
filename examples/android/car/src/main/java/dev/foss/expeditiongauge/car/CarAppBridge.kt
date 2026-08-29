package dev.foss.expeditiongauge.car

import android.graphics.Bitmap
import dev.foss.expeditiongauge.car.aaparkedvoice.AaParkedVoice

/** In-process bridge from phone app to car UI (same APK). */
interface CarAppBridge {
    fun isAndroidAutoEnabled(): Boolean
    /** [displaySpec] comes from CarContext only — never phone Display.rotation. */
    fun hudTiles(displaySpec: AaDisplaySpec = AaDisplaySpec.DEFAULT): CarHudTiles
    /** Drive HUD image + optional alert rows (Pane fallback / Message content). */
    fun driveHud(displaySpec: AaDisplaySpec = AaDisplaySpec.DEFAULT): DriveHudContent
    /**
     * Native Drive HUD bitmap for Surface painting (immutable copy).
     * [cubePxOverride] / [orientation] match host Surface visible layout when set.
     */
    fun driveHudBitmap(
        displaySpec: AaDisplaySpec = AaDisplaySpec.DEFAULT,
        cubePxOverride: Int? = null,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
    ): Bitmap?
    fun metricValues(): Map<String, String>
    fun isRecording(): Boolean
    /** Returns true if the request was accepted (or already recording). Never throws. */
    fun startRecording(): Boolean
    /** Returns true if the request was accepted (or already stopped). Never throws. */
    fun stopRecording(): Boolean
    /** Record/Stop plus parked-only spoken confirmation. */
    fun toggleRecord(): Boolean {
        val start = !isRecording()
        val ok = if (start) startRecording() else stopRecording()
        if (ok && AaParkedVoice.canAnnounce(isVehicleParked())) {
            speakParkedVoice(AaParkedVoice.phrase(start))
        }
        return ok
    }
    fun speakParkedVoice(phrase: String) = Unit
    fun markEvent(): Boolean
    /**
     * Returns false immediately if Zero is unavailable (e.g. non-phone fusion).
     * Otherwise accepts and runs async. Never throws.
     */
    fun zeroAttitude(): Boolean
    /**
     * Advances attitude display (inclinometer styles → G-meter → compass).
     * Never throws; returns true if the request was accepted.
     */
    fun cycleAttitudeDisplay(): Boolean
    /**
     * Saves the current Drive HUD bitmap to Pictures/ExpeditionGauge.
     * Returns false if no bitmap is available or MediaStore insert fails.
     */
    fun captureAaScreenshot(): Boolean
    /** Persisted mute for alert beep/TTS (Settings + AA share the same flag). */
    fun isAlertsMuted(): Boolean
    fun setAlertsMuted(muted: Boolean): Boolean
    fun toggleAlertsMuted(): Boolean = setAlertsMuted(!isAlertsMuted())
    fun setInvalidationListener(listener: (() -> Unit)?)
    /** Optional HU toast for async failures (storage full, etc.). */
    fun setToastHandler(handler: ((String) -> Unit)?)
    /** Keep IMU/GPS/BLE alive while the car session is connected. */
    fun onCarSessionStarted()
    fun onCarSessionStopped()
    /** True when speed is unknown or under the parked threshold. */
    fun isVehicleParked(): Boolean = false
    /** Stored DTC rows for the parked detail pane (empty while moving). */
    fun parkedDtcRows(): List<DriveHudRow> = emptyList()
}

object CarAppBridgeRegistry {
    @Volatile
    var bridge: CarAppBridge? = null

    /** True while an AA Screen is started (inclinometer alerts use the car nav route). */
    @Volatile
    var sessionLive: Boolean = false
}
