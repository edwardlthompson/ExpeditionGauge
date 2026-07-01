package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
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
    private var enabled: Boolean = false

    @Volatile
    private var allowlist: Set<String> = DEFAULT_ALLOWLIST

    @Volatile
    private var snapshot: TelemetrySnapshot = TelemetrySnapshot.empty()

    @Volatile
    private var useMetric: Boolean = true

    init {
        scope.launch {
            combine(
                settings.androidAutoEnabled,
                settings.androidAutoMetricAllowlist,
                settings.speedUnit,
                services.telemetryBus.snapshots,
            ) { autoEnabled, metrics, speedUnit, telem ->
                Quad(autoEnabled, metrics, speedUnit, telem)
            }.collect { (autoEnabled, metrics, speedUnit, telem) ->
                enabled = autoEnabled
                allowlist = metrics
                useMetric = speedUnit == SpeedUnit.METRIC
                snapshot = telem
                FeatureFlags.androidAutoEnabled = autoEnabled
            }
        }
    }

    override fun isAndroidAutoEnabled(): Boolean = enabled && FeatureFlags.androidAutoCapable

    override fun allowedMetricKeys(): Set<String> = allowlist

    override fun metricValues(): Map<String, String> = snapshot.toCarMetrics(useMetric)

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

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    companion object {
        val DEFAULT_ALLOWLIST: Set<String> = CarTelemetryHost.defaultPriority.take(6).toSet()

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
