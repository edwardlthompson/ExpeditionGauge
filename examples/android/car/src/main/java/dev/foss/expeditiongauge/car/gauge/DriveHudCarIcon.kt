package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.HudStripOrientation
import java.util.concurrent.ConcurrentHashMap

/** Size+orientation keyed Drive HUD bitmaps (immutable copies). Max one renderer per orientation. */
object DriveHudCarIcon {
    private data class PoolKey(
        val cubePx: Int,
        val orientation: HudStripOrientation,
        val footerPx: Int,
    )

    private val pool = ConcurrentHashMap<PoolKey, DriveHudBitmapRenderer>()

    fun from(
        pitchDeg: Float,
        rollDeg: Float,
        attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitchThresholdDeg: Float?,
        maxRollThresholdDeg: Float?,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        coordsLabel: String = "",
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        cubePx: Int,
        darkBackground: Boolean = true,
        gpsLinked: Boolean = false,
        obdLinked: Boolean = false,
        tpmsLinked: Boolean = false,
        imuLinked: Boolean = false,
        speedAlert: Boolean = false,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
        dtcFooterLine: String? = null,
    ): CarIcon {
        val bmp = renderBitmap(
            pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
            speedLabel, headingLabel, altLabel, coordsLabel, fl, fr, rl, rr, cubePx, darkBackground,
            gpsLinked, obdLinked, tpmsLinked, imuLinked, speedAlert, orientation, dtcFooterLine,
        )
        return CarIcon.Builder(IconCompat.createWithBitmap(bmp)).build()
    }

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitchThresholdDeg: Float?,
        maxRollThresholdDeg: Float?,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        coordsLabel: String = "",
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        cubePx: Int,
        darkBackground: Boolean = true,
        gpsLinked: Boolean = false,
        obdLinked: Boolean = false,
        tpmsLinked: Boolean = false,
        imuLinked: Boolean = false,
        speedAlert: Boolean = false,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
        dtcFooterLine: String? = null,
        throttlePct: Float? = null,
        pedalFlashOn: Boolean = true,
    ): Bitmap {
        val clamped = cubePx.coerceIn(AaDisplaySpec.MIN_CUBE_PX, AaDisplaySpec.MAX_SURFACE_CUBE_PX)
        // ROW always reserves the DTC band (empty when no codes); COLUMN never has a footer.
        val footerPx =
            if (orientation == HudStripOrientation.ROW) {
                DriveHudBitmapRenderer.footerPxFor(clamped)
            } else {
                0
            }
        val key = PoolKey(clamped, orientation, footerPx)
        pool.keys.filter { it.orientation == orientation && it != key }.forEach { pool.remove(it) }
        val rendered = pool.getOrPut(key) {
            DriveHudBitmapRenderer(clamped, orientation, footerPx)
        }.render(
            pitchDeg = pitchDeg, rollDeg = rollDeg, attitudeMode = attitudeMode,
            pitchAlert = pitchAlert, rollAlert = rollAlert,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
            yawDeg = yawDeg, latG = latG, lonG = lonG,
            speedLabel = speedLabel, headingLabel = headingLabel, altLabel = altLabel,
            coordsLabel = coordsLabel, fl = fl, fr = fr, rl = rl, rr = rr,
            darkBackground = darkBackground, gpsLinked = gpsLinked, obdLinked = obdLinked,
            tpmsLinked = tpmsLinked, imuLinked = imuLinked, speedAlert = speedAlert,
            dtcFooterLine = dtcFooterLine,
            throttlePct = throttlePct,
            pedalFlashOn = pedalFlashOn,
        )
        return rendered.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        style: InclinometerStyle,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitchThresholdDeg: Float?,
        maxRollThresholdDeg: Float?,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        coordsLabel: String = "",
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        cubePx: Int,
        darkBackground: Boolean = true,
    ): Bitmap = DriveHudCarIconLegacy.renderBitmap(
        pitchDeg, rollDeg, style, pitchAlert, rollAlert,
        maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
        speedLabel, headingLabel, altLabel, coordsLabel, fl, fr, rl, rr, cubePx, darkBackground,
    )
}
