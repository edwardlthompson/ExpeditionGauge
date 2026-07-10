package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.car.gauge.InclinometerCarIcon
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AndroidAutoBridge(
    private val services: ExpeditionGaugeServices,
    private val settings: SettingsPreferences,
    private val calibrationStore: CalibrationStore,
    scope: CoroutineScope,
) : CarAppBridge {

    @Volatile private var snapshot: TelemetrySnapshot = TelemetrySnapshot.empty()
    @Volatile private var speedUnit: SpeedUnit = SpeedUnit.METRIC
    @Volatile private var pressureUnit: PressureUnit = PressureUnit.PSI
    @Volatile private var tempUnit: TempUnit = TempUnit.CELSIUS
    @Volatile private var activeAlerts: Set<AlertType> = emptySet()
    @Volatile private var alertThresholds: AlertThresholds = AlertThresholds()
    @Volatile private var inclinometerStyle: InclinometerStyle = InclinometerStyle.LADDER
    @Volatile private var invalidationListener: (() -> Unit)? = null

    private val invalidation = AaScreenInvalidation()

    init {
        scope.launch {
            combine(
                combine(
                    settings.speedUnit,
                    settings.pressureUnit,
                    settings.tempUnit,
                    services.telemetryBus.snapshots,
                ) { spd, pressure, temp, telem ->
                    AndroidAutoTelemetryState(spd, pressure, temp, telem)
                },
                services.alertService.activeAlerts,
                services.alertThresholdsPreferences.thresholds,
                settings.inclinometerStyle,
            ) { telemState, alerts, thresholds, style ->
                AndroidAutoBridgeState(
                    speedUnit = telemState.speedUnit,
                    pressureUnit = telemState.pressureUnit,
                    tempUnit = telemState.tempUnit,
                    snapshot = telemState.snapshot,
                    alerts = alerts,
                    thresholds = thresholds,
                ) to style
            }.collect { (state, style) ->
                speedUnit = state.speedUnit
                pressureUnit = state.pressureUnit
                tempUnit = state.tempUnit
                snapshot = state.snapshot
                activeAlerts = state.alerts
                alertThresholds = state.thresholds
                inclinometerStyle = style
                FeatureFlags.androidAutoEnabled = FeatureFlags.androidAutoCapable
                maybeInvalidate()
            }
        }
    }

    override fun isAndroidAutoEnabled(): Boolean = FeatureFlags.androidAutoCapable

    override fun hudTiles(): CarHudTiles {
        val built = CarHudTileBuilder.build(snapshot, speedUnit, pressureUnit, tempUnit)
        val icon = runCatching {
            InclinometerCarIcon.fromAttitude(
                pitchDeg = snapshot.pitchDeg,
                rollDeg = snapshot.rollDeg,
                style = inclinometerStyle,
                pitchAlert = AlertType.PITCH in activeAlerts,
                rollAlert = AlertType.ROLL in activeAlerts,
                maxPitchThresholdDeg = alertThresholds.maxPitchDeg,
                maxRollThresholdDeg = alertThresholds.maxRollDeg,
            )
        }.getOrNull()
        return built.copy(gMeter = built.gMeter.copy(image = icon))
    }

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

    override fun zeroAttitude(): Boolean = runBlocking {
        if (snapshot.fusionSource != "phone") return@runBlocking false
        calibrationStore.zeroToCurrentDisplay(
            snapshot.pitchDeg,
            snapshot.rollDeg,
            displayRotation = 0,
        )
        maybeInvalidate(force = true)
        true
    }

    override fun setInvalidationListener(listener: (() -> Unit)?) {
        invalidationListener = listener
    }

    private fun maybeInvalidate(force: Boolean = false) {
        val listener = invalidationListener ?: return
        invalidation.maybeInvalidate(snapshot.pitchDeg, snapshot.rollDeg, force, listener)
    }

    companion object {
        const val AA_INVALIDATE_MIN_INTERVAL_MS = AaScreenInvalidation.AA_INVALIDATE_MIN_INTERVAL_MS
    }
}
