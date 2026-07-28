package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap

/** Legacy inclinometer-only overload for older callers. */
internal object DriveHudCarIconLegacy {
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
        coordsLabel: String,
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        cubePx: Int,
        darkBackground: Boolean,
    ): Bitmap = DriveHudCarIcon.renderBitmap(
        pitchDeg, rollDeg, AaAttitudeMode.fromInclinometerStyle(style),
        pitchAlert, rollAlert, maxPitchThresholdDeg, maxRollThresholdDeg,
        yawDeg, latG, lonG, speedLabel, headingLabel, altLabel, coordsLabel,
        fl, fr, rl, rr, cubePx, darkBackground,
    )
}
