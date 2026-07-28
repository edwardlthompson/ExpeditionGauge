package dev.foss.expeditiongauge.car

import android.content.Context
import android.graphics.Bitmap
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.acquireSensors
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.releaseSensors
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope

class AndroidAutoBridge(
    private val services: ExpeditionGaugeServices,
    private val settings: SettingsPreferences,
    private val accessibility: AccessibilityPreferences,
    private val scope: CoroutineScope,
    private val appContext: Context,
) : CarAppBridge {

    @Volatile private var snapshot: TelemetrySnapshot = TelemetrySnapshot.empty()
    @Volatile private var speedUnit: SpeedUnit = SpeedUnit.METRIC
    @Volatile private var pressureUnit: PressureUnit = PressureUnit.PSI
    @Volatile private var tempUnit: TempUnit = TempUnit.CELSIUS
    @Volatile private var activeAlerts: Set<AlertType> = emptySet()
    @Volatile private var alertThresholds: AlertThresholds = AlertThresholds()
    @Volatile private var attitudeGaugeMode: AttitudeGaugeMode = AttitudeGaugeMode.INCLINOMETER_LADDER
    @Volatile private var recording: Boolean = false
    @Volatile private var alertsMuted: Boolean = false
    @Volatile private var storedDtcs: List<DtcEntry> = emptyList()
    @Volatile private var invalidationListener: (() -> Unit)? = null
    @Volatile private var toastHandler: ((String) -> Unit)? = null
    private val invalidation = AaScreenInvalidation()
    private val hudCompose = AndroidAutoBridgeHudCompose(this.appContext)
    private val mute = AndroidAutoBridgeMute(accessibility, scope) { muted ->
        alertsMuted = muted
        maybeInvalidate(force = true)
    }
    private val mutators = AndroidAutoBridgeMutators(
        services = services, scope = scope, settings = settings,
        toast = { toastHandler }, invalidateForce = { maybeInvalidate(force = true) },
    )
    private val dtc = AndroidAutoBridgeDtc(
        scope = scope, storedDtcsFlow = services.obdManager.storedDtcs,
        onDtcs = { storedDtcs = it }, invalidateForce = { maybeInvalidate(force = true) },
    )

    init {
        AndroidAutoBridgeCollectors.start(services, settings, scope) { state, mode, isRecording ->
            speedUnit = state.speedUnit
            pressureUnit = state.pressureUnit
            tempUnit = state.tempUnit
            snapshot = state.snapshot
            activeAlerts = state.alerts
            alertThresholds = state.thresholds
            attitudeGaugeMode = mode
            recording = isRecording
            maybeInvalidate()
        }
        mute.startCollect()
        dtc.startCollect()
    }

    override fun isAndroidAutoEnabled(): Boolean = FeatureFlags.androidAutoCapable

    override fun hudTiles(displaySpec: AaDisplaySpec): CarHudTiles =
        hudCompose.hudTiles(snapshot, speedUnit, pressureUnit, tempUnit)

    override fun driveHud(displaySpec: AaDisplaySpec): DriveHudContent =
        composeHud(displaySpec, null)

    override fun driveHudBitmap(
        displaySpec: AaDisplaySpec,
        cubePxOverride: Int?,
        orientation: HudStripOrientation,
    ): Bitmap? {
        composeHud(displaySpec, cubePxOverride, orientation)
        return hudCompose.snapshotBitmap()
    }

    private fun composeHud(
        displaySpec: AaDisplaySpec,
        cubePxOverride: Int?,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
    ): DriveHudContent =
        hudCompose.composeHud(
            snapshot, attitudeGaugeMode, activeAlerts, alertThresholds, displaySpec,
            speedUnit, pressureUnit, tempUnit, cubePxOverride, orientation,
            storedDtcs = storedDtcs,
        )

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

    override fun cycleAttitudeDisplay(): Boolean = mutators.cycleAttitudeDisplay()

    override fun captureAaScreenshot(): Boolean =
        AndroidAutoBridgeActions.captureScreenshot(
            hudCompose.snapshotBitmap(), appContext, toastHandler,
        )

    override fun isAlertsMuted(): Boolean = alertsMuted
    override fun setAlertsMuted(muted: Boolean): Boolean = mute.setAlertsMuted(muted)
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
        const val AA_INVALIDATE_MIN_INTERVAL_MS =
            AaScreenInvalidation.AA_INVALIDATE_MIN_INTERVAL_MS
    }
}
