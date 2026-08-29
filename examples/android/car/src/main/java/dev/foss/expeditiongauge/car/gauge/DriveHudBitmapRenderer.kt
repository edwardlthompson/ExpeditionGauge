package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import dev.foss.expeditiongauge.car.HudStripOrientation

/**
 * ROW = Attitude|Telemetry|TPMS + DTC footer; COLUMN = Attitude|Telemetry.
 */
class DriveHudBitmapRenderer(
    private val cubePx: Int = DEFAULT_CUBE_PX,
    private val orientation: HudStripOrientation = HudStripOrientation.ROW,
    private val footerPx: Int = 0,
    private val textScale: Float = 1f,
    private val highContrast: Boolean = false,
) {
    private val cubeCount = HudStripOrientation.cubeCount(orientation)
    private val stripW = if (orientation == HudStripOrientation.ROW) cubePx * cubeCount else cubePx
    private val stripH = if (orientation == HudStripOrientation.ROW) cubePx + footerPx.coerceAtLeast(0) else cubePx * cubeCount
    private val bitmap: Bitmap = Bitmap.createBitmap(stripW, stripH, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val cubes = DriveHudCubeDraw(canvas, cubePx)

    val widthPx: Int get() = stripW
    val heightPx: Int get() = stripH
    val cubeBandHeightPx: Int get() = cubePx

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
        gpsLinked: Boolean = false,
        obdLinked: Boolean = false,
        tpmsLinked: Boolean = false,
        imuLinked: Boolean = false,
        speedAlert: Boolean = false,
        dtcFooterLine: String? = null,
        throttlePct: Float? = null,
        pedalFlashOn: Boolean = true,
    ): Bitmap {
        cubes.pedalThrottlePct = throttlePct
        cubes.pedalLonG = lonG ?: 0f
        cubes.pedalFlashOn = pedalFlashOn
        val theme = DriveHudTheme.forDarkMode(darkBackground, textScale, highContrast)
        canvas.drawColor(theme.background)
        val gap = (cubePx * 0.04f).toInt().coerceAtLeast(2)
        val cell = (cubePx - gap).coerceAtLeast(48)
        when (orientation) {
            HudStripOrientation.COLUMN -> drawColumn(
                gap, cell, theme, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
                maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
                speedLabel, headingLabel, altLabel, coordsLabel,
                gpsLinked, obdLinked, tpmsLinked, imuLinked, speedAlert,
            )
            HudStripOrientation.ROW -> {
                drawRow(
                    gap, cell, theme, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
                    maxPitchThresholdDeg, maxRollThresholdDeg, yawDeg, latG, lonG,
                    speedLabel, headingLabel, altLabel, coordsLabel, fl, fr, rl, rr,
                    gpsLinked, obdLinked, tpmsLinked, imuLinked, speedAlert,
                )
                if (footerPx > 0) {
                    DriveHudDtcFooterPaint.draw(
                        canvas, dtcFooterLine.orEmpty(), theme, cubePx, footerPx, stripW,
                        cubeGapPx = gap,
                    )
                }
            }
        }
        return bitmap
    }

    private fun drawColumn(
        gap: Int, cell: Int, theme: DriveHudTheme,
        pitchDeg: Float, rollDeg: Float, attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean, rollAlert: Boolean,
        maxPitch: Float?, maxRoll: Float?, yawDeg: Float?, latG: Float?, lonG: Float?,
        speedLabel: String, headingLabel: String, altLabel: String, coordsLabel: String,
        gpsLinked: Boolean, obdLinked: Boolean, tpmsLinked: Boolean, imuLinked: Boolean,
        speedAlert: Boolean,
    ) {
        val x = gap / 2
        drawAttitude(x, gap / 2, cell, theme, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitch, maxRoll, yawDeg, latG, lonG)
        cubes.drawTelemetryCube(
            x, cubePx + gap / 2, cell, theme,
            speedLabel, headingLabel, altLabel, coordsLabel,
            gpsLinked = gpsLinked, obdLinked = obdLinked, tpmsLinked = tpmsLinked,
            imuLinked = imuLinked, speedAlert = speedAlert,
        )
    }

    private fun drawRow(
        gap: Int, cell: Int, theme: DriveHudTheme,
        pitchDeg: Float, rollDeg: Float, attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean, rollAlert: Boolean,
        maxPitch: Float?, maxRoll: Float?, yawDeg: Float?, latG: Float?, lonG: Float?,
        speedLabel: String, headingLabel: String, altLabel: String, coordsLabel: String,
        fl: String, fr: String, rl: String, rr: String,
        gpsLinked: Boolean, obdLinked: Boolean, tpmsLinked: Boolean, imuLinked: Boolean,
        speedAlert: Boolean,
    ) {
        val y = gap / 2
        drawAttitude(0, y, cell, theme, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitch, maxRoll, yawDeg, latG, lonG)
        cubes.drawTelemetryCube(
            cubePx + gap / 2, y, cell, theme,
            speedLabel, headingLabel, altLabel, coordsLabel,
            gpsLinked = gpsLinked, obdLinked = obdLinked, tpmsLinked = tpmsLinked,
            imuLinked = imuLinked, speedAlert = speedAlert,
        )
        cubes.drawTpmsCube(cubePx * 2 + gap / 2, y, cell, theme, fl, fr, rl, rr)
    }

    private fun drawAttitude(
        x: Int, y: Int, size: Int, theme: DriveHudTheme,
        pitchDeg: Float, rollDeg: Float, attitudeMode: AaAttitudeMode,
        pitchAlert: Boolean, rollAlert: Boolean,
        maxPitch: Float?, maxRoll: Float?, yawDeg: Float?, latG: Float?, lonG: Float?,
    ) {
        val attitude = DriveHudAttitudeDraw.renderInner(
            cubes, size, pitchDeg, rollDeg, attitudeMode, pitchAlert, rollAlert,
            maxPitch, maxRoll, yawDeg, latG, lonG,
        )
        cubes.drawAttitudeBitmap(x, y, size, theme, attitude)
    }

    companion object {
        const val DEFAULT_CUBE_PX = 280
        /** Permanent ROW DTC band (~18% of cube; readable after Surface scale-to-fit). */
        fun footerPxFor(cubePx: Int): Int = (cubePx * 18 / 100).coerceIn(52, 72)
    }
}
