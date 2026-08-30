package dev.foss.expeditiongauge.car

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.toAaAttitudeMode
import dev.foss.expeditiongauge.obd.dtc.DtcCarousel
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.car.gauge.PedalBarLogic
import dev.foss.expeditiongauge.telemetry.SensorLinkState
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Builds AA Pane Drive HUD — 3×1 cube bitmap; no duplicate text rows. */
class AaHudComposer(appContext: Context) {
    private val fallback = CarIcon.Builder(
        IconCompat.createWithResource(appContext, R.drawable.ic_wheel),
    ).build()
    private var cached: CarIcon? = null
    private var cachedKey: AaHudDriveKey? = null
    private var lastBitmap: Bitmap? = null

    fun composeDriveHud(
        snapshot: TelemetrySnapshot,
        attitudeMode: AttitudeGaugeMode,
        alerts: Set<AlertType>,
        thresholds: AlertThresholds,
        displaySpec: AaDisplaySpec,
        speedUnit: SpeedUnit = SpeedUnit.METRIC,
        pressureUnit: PressureUnit = PressureUnit.PSI,
        tempUnit: TempUnit = TempUnit.CELSIUS,
        cubePxOverride: Int? = null,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
        storedDtcs: List<DtcEntry> = emptyList(),
        nowMs: Long = SystemClock.elapsedRealtime(),
    ): DriveHudContent {
        val labels = CarHudTileBuilder.labels(snapshot, speedUnit, pressureUnit, tempUnit)
        val links = SensorLinkState.from(snapshot)
        val cubePx = cubePxOverride?.coerceIn(
            AaDisplaySpec.MIN_CUBE_PX,
            AaDisplaySpec.MAX_SURFACE_CUBE_PX,
        ) ?: displaySpec.cubeSizePx
        val dtcLine =
            if (orientation == HudStripOrientation.ROW) {
                DtcCarousel.frame(storedDtcs, nowMs)?.line()
            } else {
                null
            }
        val pedal = PedalBarLogic.from(snapshot.throttlePct, snapshot.lonG)
        val key = AaHudDriveKey(
            pitchDeg = snapshot.pitchDeg,
            rollDeg = snapshot.rollDeg,
            attitudeMode = attitudeMode.toAaAttitudeMode(),
            pitchAlert = AlertType.PITCH in alerts,
            rollAlert = AlertType.ROLL in alerts,
            maxPitch = thresholds.maxPitchDeg,
            maxRoll = thresholds.maxRollDeg,
            speed = labels.speedLabel,
            heading = labels.headingLabel,
            alt = labels.altLabel,
            coords = labels.coordsLabel,
            fl = labels.flBitmap,
            fr = labels.frBitmap,
            rl = labels.rlBitmap,
            rr = labels.rrBitmap,
            cubePx = cubePx,
            dark = displaySpec.isDarkMode,
            gpsLinked = links.gpsLinked,
            obdLinked = links.obdLinked,
            tpmsLinked = links.tpmsLinked,
            imuLinked = links.imuLinked,
            speedAlert = AlertType.SPEED in alerts,
            orientation = orientation,
            dtcFooterLine = dtcLine,
            pedalQ = PedalBarLogic.quantize(pedal),
            pedalFlashOn = !pedal.flashThrottle && !pedal.flashBrake ||
                (nowMs / 280L) % 2L == 0L,
            textScale = displaySpec.textScale,
            highContrast = displaySpec.isHighContrast,
            satelliteCount = snapshot.numSatellites ?: 0,
        )
        val image = reuseOrBuild(key, snapshot)
        return DriveHudContent(image = image, rows = AaHudComposerRender.alertRows(key))
    }

    fun snapshotBitmap(): Bitmap? = lastBitmap?.copy(Bitmap.Config.ARGB_8888, false)

    private fun reuseOrBuild(key: AaHudDriveKey, snapshot: TelemetrySnapshot): CarIcon {
        val prev = cachedKey
        val prevIcon = cached
        if (prev != null && prevIcon != null && prev.sameForReuse(key)) return prevIcon
        val bmp = AaHudComposerRender.renderBitmap(key, snapshot)
        lastBitmap = bmp
        val icon = AaHudComposerRender.toPaneIcon(bmp, key, fallback)
        cachedKey = key
        cached = icon
        return icon
    }

    companion object {
        const val ATTITUDE_EPS_DEG = 0.1f
    }
}
