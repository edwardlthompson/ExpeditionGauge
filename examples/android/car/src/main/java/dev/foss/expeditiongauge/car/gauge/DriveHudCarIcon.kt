package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.AaDisplaySpec
import java.util.concurrent.ConcurrentHashMap

/** Size-keyed 3×1 Drive HUD bitmaps for PaneTemplate (immutable copies). */
object DriveHudCarIcon {
    private val pool = ConcurrentHashMap<Int, DriveHudBitmapRenderer>()

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
    ): CarIcon {
        val bmp = renderBitmap(
            pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
            speedLabel, headingLabel, altLabel, coordsLabel, fl, fr, rl, rr, cubePx, darkBackground,
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
    ): Bitmap {
        val clamped = cubePx.coerceIn(AaDisplaySpec.MIN_CUBE_PX, AaDisplaySpec.MAX_SURFACE_CUBE_PX)
        val rendered = pool.getOrPut(clamped) { DriveHudBitmapRenderer(clamped) }.render(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            attitudeMode = attitudeMode,
            pitchAlert = pitchAlert,
            rollAlert = rollAlert,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
            yawDeg = yawDeg,
            latG = latG,
            lonG = lonG,
            speedLabel = speedLabel,
            headingLabel = headingLabel,
            altLabel = altLabel,
            coordsLabel = coordsLabel,
            fl = fl,
            fr = fr,
            rl = rl,
            rr = rr,
            darkBackground = darkBackground,
        )
        return rendered.copy(Bitmap.Config.ARGB_8888, false)
    }

    /** Legacy overload for inclinometer-only callers. */
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
    ): Bitmap = renderBitmap(
        pitchDeg, rollDeg, AaAttitudeMode.fromInclinometerStyle(style),
        pitchAlert, rollAlert, maxPitchThresholdDeg, maxRollThresholdDeg,
        yawDeg, latG, lonG, speedLabel, headingLabel, altLabel, coordsLabel,
        fl, fr, rl, rr, cubePx, darkBackground,
    )
}
