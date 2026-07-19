package dev.foss.expeditiongauge.car

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.car.gauge.DriveHudCarIcon
import dev.foss.expeditiongauge.car.gauge.DriveHudLetterbox
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.toAaAttitudeMode
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Builds AA Pane Drive HUD — 3×1 cube bitmap; no duplicate text rows. */
class AaHudComposer(appContext: Context) {
    private val fallback = resourceIcon(appContext, R.drawable.ic_wheel)
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
    ): DriveHudContent {
        val labels = CarHudTileBuilder.labels(snapshot, speedUnit, pressureUnit, tempUnit)
        val cubePx = cubePxOverride?.coerceIn(
            AaDisplaySpec.MIN_CUBE_PX,
            AaDisplaySpec.MAX_SURFACE_CUBE_PX,
        ) ?: displaySpec.cubeSizePx
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
        )
        val image = reuseOrBuild(key, snapshot)
        val rows = alertRows(key)
        return DriveHudContent(image = image, rows = rows)
    }

    fun snapshotBitmap(): Bitmap? = lastBitmap?.copy(Bitmap.Config.ARGB_8888, false)

    private fun alertRows(key: AaHudDriveKey): List<DriveHudRow> {
        if (!key.pitchAlert && !key.rollAlert) return emptyList()
        val text = when {
            key.pitchAlert && key.rollAlert -> "Pitch & roll"
            key.pitchAlert -> "Pitch"
            else -> "Roll"
        }
        return listOf(DriveHudRow("Alert", text))
    }

    private fun reuseOrBuild(key: AaHudDriveKey, snapshot: TelemetrySnapshot): CarIcon {
        val prev = cachedKey
        val prevIcon = cached
        if (prev != null && prevIcon != null && prev.sameForReuse(key)) return prevIcon
        val bmp = runCatching {
            DriveHudCarIcon.renderBitmap(
                pitchDeg = snapshot.pitchDeg,
                rollDeg = snapshot.rollDeg,
                attitudeMode = key.attitudeMode,
                pitchAlert = key.pitchAlert,
                rollAlert = key.rollAlert,
                maxPitchThresholdDeg = key.maxPitch,
                maxRollThresholdDeg = key.maxRoll,
                yawDeg = snapshot.bodyYawDeg ?: snapshot.headingDeg,
                latG = snapshot.latG,
                lonG = snapshot.lonG,
                speedLabel = key.speed,
                headingLabel = key.heading,
                altLabel = key.alt,
                coordsLabel = key.coords,
                fl = key.fl,
                fr = key.fr,
                rl = key.rl,
                rr = key.rr,
                cubePx = key.cubePx,
                darkBackground = key.dark,
            )
        }.onFailure { Log.e(TAG, "Drive HUD render failed", it) }.getOrNull()
        lastBitmap = bmp
        val paneBmp = bmp?.let { DriveHudLetterbox.toSquare(it, key.dark) }
        val icon = paneBmp?.let { CarIcon.Builder(IconCompat.createWithBitmap(it)).build() } ?: fallback
        cachedKey = key
        cached = icon
        return icon
    }

    private fun resourceIcon(context: Context, drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(context, drawableRes)).build()

    companion object {
        private const val TAG = "AaHudComposer"
        const val ATTITUDE_EPS_DEG = 0.1f
    }
}
