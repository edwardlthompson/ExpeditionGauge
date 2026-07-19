package dev.foss.expeditiongauge.car

import android.content.Context
import android.graphics.Bitmap
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.acquireSensors
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.media.HudScreenshotIo
import dev.foss.expeditiongauge.releaseSensors
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope

class AndroidAutoBridge(
    private val services: ExpeditionGaugeServices,
    private val settings: SettingsPreferences,
    scope: CoroutineScope,
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
    @Volatile private var invalidationListener: (() -> Unit)? = null
    @Volatile private var toastHandler: ((String) -> Unit)? = null

    private val invalidation = AaScreenInvalidation()
    private val hudComposer = AaHudComposer(this.appContext)
    private val mutators = AndroidAutoBridgeMutators(
        services = services,
        scope = scope,
        settings = settings,
        toast = { toastHandler },
        invalidateForce = { maybeInvalidate(force = true) },
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
    }

    override fun isAndroidAutoEnabled(): Boolean = FeatureFlags.androidAutoCapable

    override fun hudTiles(displaySpec: AaDisplaySpec): CarHudTiles {
        val labels = CarHudTileBuilder.labels(snapshot, speedUnit, pressureUnit, tempUnit)
        return CarHudTiles(
            gMeter = CarHudTile("Attitude", labels.attitudeLine, ""),
            telemetry = CarHudTile("Telemetry", labels.telemetrySecondary, ""),
            tpms = CarHudTile("TPMS", labels.tpmsSecondary, ""),
        )
    }

    override fun driveHud(displaySpec: AaDisplaySpec): DriveHudContent =
        composeHud(displaySpec, cubePxOverride = null)

    override fun driveHudBitmap(
        displaySpec: AaDisplaySpec,
        cubePxOverride: Int?,
    ): Bitmap? {
        composeHud(displaySpec, cubePxOverride)
        return hudComposer.snapshotBitmap()
    }

    private fun composeHud(displaySpec: AaDisplaySpec, cubePxOverride: Int?): DriveHudContent =
        hudComposer.composeDriveHud(
            snapshot,
            attitudeGaugeMode,
            activeAlerts,
            alertThresholds,
            displaySpec,
            speedUnit = speedUnit,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            cubePxOverride = cubePxOverride,
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

    override fun captureAaScreenshot(): Boolean {
        val bmp = hudComposer.snapshotBitmap() ?: run {
            toastHandler?.invoke("Screenshot unavailable")
            return false
        }
        val ok = runCatching {
            HudScreenshotIo.insertBitmap(appContext, bmp, suffix = "_AA")
        }.getOrDefault(false)
        toastHandler?.invoke(if (ok) "Screenshot saved" else "Screenshot failed")
        return ok
    }

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
