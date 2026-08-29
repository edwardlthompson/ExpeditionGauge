package dev.foss.expeditiongauge.car

import android.graphics.Bitmap
import android.util.Log
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.gauge.DriveHudCarIcon
import dev.foss.expeditiongauge.car.gauge.DriveHudLetterbox
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Bitmap/cache helpers for [AaHudComposer]. */
internal object AaHudComposerRender {
    private const val TAG = "AaHudComposer"

    fun alertRows(key: AaHudDriveKey): List<DriveHudRow> {
        if (!key.pitchAlert && !key.rollAlert) return emptyList()
        val text = when {
            key.pitchAlert && key.rollAlert -> "Pitch & roll"
            key.pitchAlert -> "Pitch"
            else -> "Roll"
        }
        return listOf(DriveHudRow("Alert", text))
    }

    fun renderBitmap(key: AaHudDriveKey, snapshot: TelemetrySnapshot): Bitmap? =
        runCatching {
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
                gpsLinked = key.gpsLinked,
                obdLinked = key.obdLinked,
                tpmsLinked = key.tpmsLinked,
                imuLinked = key.imuLinked,
                speedAlert = key.speedAlert,
                orientation = key.orientation,
                dtcFooterLine = key.dtcFooterLine,
                throttlePct = snapshot.throttlePct,
                pedalFlashOn = key.pedalFlashOn,
                textScale = key.textScale,
            )
        }.onFailure { Log.e(TAG, "Drive HUD render failed", it) }.getOrNull()

    fun toPaneIcon(bmp: Bitmap?, key: AaHudDriveKey, fallback: CarIcon): CarIcon {
        val paneBmp = bmp?.let {
            if (key.orientation == HudStripOrientation.ROW) {
                DriveHudLetterbox.toSquare(it, key.dark)
            } else {
                it
            }
        }
        return paneBmp?.let { CarIcon.Builder(IconCompat.createWithBitmap(it)).build() } ?: fallback
    }
}
