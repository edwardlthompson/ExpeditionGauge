package dev.foss.expeditiongauge.car

import android.content.Context
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.acquireSensors
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.releaseSensors
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AndroidAutoBridge(
    private val services: ExpeditionGaugeServices,
    private val settings: SettingsPreferences,
    scope: CoroutineScope,
    appContext: Context,
) : CarAppBridge {

    @Volatile private var snapshot: TelemetrySnapshot = TelemetrySnapshot.empty()
    @Volatile private var speedUnit: SpeedUnit = SpeedUnit.METRIC
    @Volatile private var pressureUnit: PressureUnit = PressureUnit.PSI
    @Volatile private var tempUnit: TempUnit = TempUnit.CELSIUS
    @Volatile private var activeAlerts: Set<AlertType> = emptySet()
    @Volatile private var alertThresholds: AlertThresholds = AlertThresholds()
    @Volatile private var inclinometerStyle: InclinometerStyle = InclinometerStyle.LADDER
    @Volatile private var recording: Boolean = false
    @Volatile private var invalidationListener: (() -> Unit)? = null
    @Volatile private var toastHandler: ((String) -> Unit)? = null

    private val invalidation = AaScreenInvalidation()
    private val hudComposer = AaHudComposer(appContext)
    private val mutators = AndroidAutoBridgeMutators(
        services = services,
        scope = scope,
        toast = { toastHandler },
        invalidateForce = { maybeInvalidate(force = true) },
    )

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
                services.recordingWriter.recording,
            ) { telemState, alerts, thresholds, style, isRecording ->
                AndroidAutoBridgeState(
                    speedUnit = telemState.speedUnit,
                    pressureUnit = telemState.pressureUnit,
                    tempUnit = telemState.tempUnit,
                    snapshot = telemState.snapshot,
                    alerts = alerts,
                    thresholds = thresholds,
                ) to (style to isRecording)
            }.collect { (state, styleAndRec) ->
                val (style, isRecording) = styleAndRec
                speedUnit = state.speedUnit
                pressureUnit = state.pressureUnit
                tempUnit = state.tempUnit
                snapshot = state.snapshot
                activeAlerts = state.alerts
                alertThresholds = state.thresholds
                inclinometerStyle = style
                recording = isRecording
                FeatureFlags.androidAutoEnabled = FeatureFlags.androidAutoCapable
                maybeInvalidate()
            }
        }
    }

    override fun isAndroidAutoEnabled(): Boolean = FeatureFlags.androidAutoCapable

    override fun hudTiles(displaySpec: AaDisplaySpec): CarHudTiles {
        val built = CarHudTileBuilder.build(snapshot, speedUnit, pressureUnit, tempUnit)
        return hudComposer.compose(
            snapshot, inclinometerStyle, activeAlerts, alertThresholds, displaySpec, built,
        )
    }

    override fun metricValues(): Map<String, String> =
        snapshot.toCarMetrics(speedUnit == SpeedUnit.METRIC)

    override fun isRecording(): Boolean = recording

    override fun startRecording(): Boolean = mutators.startRecording(recording)

    override fun stopRecording(): Boolean = mutators.stopRecording(recording)

    override fun markEvent(): Boolean = mutators.markEvent()

    override fun zeroAttitude(): Boolean = mutators.zeroAttitude(
        fusionSource = snapshot.fusionSource,
        pitchDeg = snapshot.pitchDeg,
        rollDeg = snapshot.rollDeg,
        yawDeg = snapshot.bodyYawDeg ?: snapshot.headingDeg,
    )

    override fun setInvalidationListener(listener: (() -> Unit)?) {
        invalidationListener = listener
    }

    override fun setToastHandler(handler: ((String) -> Unit)?) {
        toastHandler = handler
    }

    override fun onCarSessionStarted() = services.acquireSensors()

    override fun onCarSessionStopped() = services.releaseSensors()

    private fun maybeInvalidate(force: Boolean = false) {
        val listener = invalidationListener ?: return
        invalidation.maybeInvalidate(snapshot.pitchDeg, snapshot.rollDeg, force, listener)
    }

    companion object {
        const val AA_INVALIDATE_MIN_INTERVAL_MS = AaScreenInvalidation.AA_INVALIDATE_MIN_INTERVAL_MS
    }
}
