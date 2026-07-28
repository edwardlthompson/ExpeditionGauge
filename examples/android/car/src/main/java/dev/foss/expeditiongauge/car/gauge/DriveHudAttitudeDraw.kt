package dev.foss.expeditiongauge.car.gauge

/** Attitude cube bitmap selection for [DriveHudBitmapRenderer]. */
internal object DriveHudAttitudeDraw {
    fun renderInner(
        cubes: DriveHudCubeDraw,
        size: Int,
        pitchDeg: Float,
        rollDeg: Float,
        attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitch: Float?,
        maxRoll: Float?,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
    ): android.graphics.Bitmap {
        val inner = cubes.attitudeInnerPx(size)
        return when (attitudeMode) {
            AaAttitudeMode.G_FORCE -> GMeterCarIcon.renderBitmap(
                pitchDeg, rollDeg, pitchAlert, rollAlert, inner,
                yawDeg, latG, lonG, darkBackground = true,
            )
            AaAttitudeMode.COMPASS_BALL -> CompassBallCarIcon.renderBitmap(
                pitchDeg = pitchDeg,
                rollDeg = rollDeg,
                yawDeg = yawDeg ?: 0f,
                cardinalsTrusted = yawDeg != null,
                sizePx = inner,
                darkBackground = true,
            )
            else -> {
                val style = attitudeMode.toInclinometerStyle() ?: InclinometerStyle.LADDER
                InclinometerCarIcon.renderBitmap(
                    pitchDeg = pitchDeg,
                    rollDeg = rollDeg,
                    style = style,
                    pitchAlert = pitchAlert,
                    rollAlert = rollAlert,
                    maxPitchThresholdDeg = maxPitch,
                    maxRollThresholdDeg = maxRoll,
                    yawDeg = yawDeg,
                    latG = latG,
                    lonG = lonG,
                    sizePx = inner,
                    darkBackground = true,
                )
            }
        }
    }
}
