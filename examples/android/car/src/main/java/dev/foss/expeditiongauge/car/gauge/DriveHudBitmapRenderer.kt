package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Drive HUD: Attitude | Telemetry | TPMS as a native **3×1** strip
 * (width = 3×cube, height = cube). Primary path is AA Surface painting.
 */
class DriveHudBitmapRenderer(
    private val cubePx: Int = DEFAULT_CUBE_PX,
) {
    private val stripW = cubePx * 3
    private val stripH = cubePx
    private val bitmap: Bitmap = Bitmap.createBitmap(stripW, stripH, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val cubes = DriveHudCubeDraw(canvas, cubePx)

    fun render(
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
        darkBackground: Boolean = true,
    ): Bitmap {
        val theme = DriveHudTheme.forDarkMode(darkBackground)
        canvas.drawColor(theme.background)
        val gap = (cubePx * 0.04f).toInt().coerceAtLeast(2)
        val cell = (cubePx - gap).coerceAtLeast(48)
        val y = gap / 2
        drawAttitudeCube(
            0, y, cell, theme, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
        )
        cubes.drawTelemetryCube(
            cubePx + gap / 2, y, cell, theme,
            speedLabel, headingLabel, altLabel, coordsLabel,
        )
        cubes.drawTpmsCube(cubePx * 2 + gap / 2, y, cell, theme, fl, fr, rl, rr)
        return bitmap
    }

    private fun drawAttitudeCube(
        x: Int, y: Int, size: Int, theme: DriveHudTheme,
        pitchDeg: Float, rollDeg: Float, attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean, rollAlert: Boolean,
        maxPitch: Float?, maxRoll: Float?,
        yawDeg: Float?, latG: Float?, lonG: Float?,
    ) {
        val inner = cubes.attitudeInnerPx(size)
        val attitude = when (attitudeMode) {
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
        cubes.drawAttitudeBitmap(x, y, size, theme, attitude)
    }

    companion object {
        const val DEFAULT_CUBE_PX = 280
    }
}
