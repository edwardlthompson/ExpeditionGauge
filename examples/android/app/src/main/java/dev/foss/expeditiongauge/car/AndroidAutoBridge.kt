package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AndroidAutoBridge(
    private val services: ExpeditionGaugeServices,
    private val settings: SettingsPreferences,
    scope: CoroutineScope,
) : CarAppBridge {

    @Volatile
    private var snapshot: TelemetrySnapshot = TelemetrySnapshot.empty()

    @Volatile
    private var speedUnit: SpeedUnit = SpeedUnit.METRIC

    @Volatile
    private var pressureUnit: PressureUnit = PressureUnit.PSI

    @Volatile
    private var tempUnit: TempUnit = TempUnit.CELSIUS

    @Volatile
    private var invalidationListener: (() -> Unit)? = null

    @Volatile
    private var lastInvalidateMs: Long = 0L

    init {
        scope.launch {
            combine(
                settings.speedUnit,
                settings.pressureUnit,
                settings.tempUnit,
                services.telemetryBus.snapshots,
            ) { spd, pressure, temp, telem ->
                UnitPrefs(spd, pressure, temp, telem)
            }.collect { prefs ->
                speedUnit = prefs.speedUnit
                pressureUnit = prefs.pressureUnit
                tempUnit = prefs.tempUnit
                snapshot = prefs.snapshot
                FeatureFlags.androidAutoEnabled = FeatureFlags.androidAutoCapable
                maybeInvalidate()
            }
        }
    }

    override fun isAndroidAutoEnabled(): Boolean = FeatureFlags.androidAutoCapable

    override fun hudTiles(): CarHudTiles = CarHudTileBuilder.build(
        snapshot = snapshot,
        speedUnit = speedUnit,
        pressureUnit = pressureUnit,
        tempUnit = tempUnit,
    )

    override fun metricValues(): Map<String, String> =
        snapshot.toCarMetrics(speedUnit == SpeedUnit.METRIC)

    override fun isRecording(): Boolean = services.recordingWriter.recording.value

    override fun startRecording(): Boolean = runBlocking {
        if (services.recordingWriter.recording.value) return@runBlocking true
        services.recordingWriter.startRecording()
        services.recordingWriter.recording.value
    }

    override fun stopRecording(): Boolean = runBlocking {
        if (!services.recordingWriter.recording.value) return@runBlocking true
        services.recordingWriter.stopRecording()
        !services.recordingWriter.recording.value
    }

    override fun markEvent(): Boolean = runBlocking {
        val sessionId = services.recordingWriter.activeSessionId.value ?: return@runBlocking false
        services.sessionEventRecorder.markEvent(sessionId, services.telemetryBus.snapshots.value)
        true
    }

    override fun setInvalidationListener(listener: (() -> Unit)?) {
        invalidationListener = listener
    }

    private fun maybeInvalidate() {
        val listener = invalidationListener ?: return
        val now = System.currentTimeMillis()
        if (now - lastInvalidateMs < INVALIDATE_MIN_INTERVAL_MS) return
        lastInvalidateMs = now
        listener()
    }

    private data class UnitPrefs(
        val speedUnit: SpeedUnit,
        val pressureUnit: PressureUnit,
        val tempUnit: TempUnit,
        val snapshot: TelemetrySnapshot,
    )

    companion object {
        private const val INVALIDATE_MIN_INTERVAL_MS = 1_000L

        fun TelemetrySnapshot.toCarMetrics(useMetric: Boolean): Map<String, String> {
            val speed = "${GaugeLogic.formatSpeedMps(speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
            val beta = driftAngleDeg?.let { GaugeLogic.formatSignedDegrees(it) } ?: "—"
            val rpmText = rpm?.let { "${it.toInt()} rpm" } ?: "—"
            val throttleText = throttlePct?.let { "${it.toInt()}%" } ?: "—"
            return mapOf(
                "speed" to speed,
                "latG" to "%.2f G".format(latG),
                "pitch" to GaugeLogic.formatSignedDegrees(pitchDeg),
                "roll" to GaugeLogic.formatSignedDegrees(rollDeg),
                "beta" to beta,
                "rpm" to rpmText,
                "throttle" to throttleText,
            )
        }
    }
}
