package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Pitch/roll inclinometer bitmaps. Callers pass **vehicle-frame** pitch/roll
 * (fusion output after calibration) — do not apply G-meter screen remaps.
 */
class InclinometerBitmapRenderer(
    private val sizePx: Int = DEFAULT_SIZE_PX,
) {
    private val bitmap: Bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val ladder = InclinometerDrawHelper(canvas, sizePx)
    private val horizon = InclinometerHorizonDraw(canvas, sizePx)
    private val dualDial = InclinometerDualDialDraw(canvas, sizePx)
    private val bubble = InclinometerBubbleDraw(canvas, sizePx)

    fun render(
        pitchDeg: Float,
        rollDeg: Float,
        style: InclinometerStyle = InclinometerStyle.LADDER,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
        labelPitchDeg: Float? = null,
        labelRollDeg: Float? = null,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
        darkBackground: Boolean = true,
    ): Bitmap {
        canvas.drawColor(
            if (darkBackground) InclinometerColor.BACKGROUND else InclinometerColor.BACKGROUND_LIGHT,
        )
        val alert = pitchAlert || rollAlert
        val labelP = labelPitchDeg ?: pitchDeg
        val labelR = labelRollDeg ?: rollDeg
        when (style) {
            InclinometerStyle.LADDER -> renderLadder(
                pitchDeg, rollDeg, pitchAlert, rollAlert,
                maxPitchThresholdDeg, maxRollThresholdDeg,
                labelP, labelR, yawDeg, latG, lonG,
            )
            InclinometerStyle.HORIZON -> horizon.draw(
                pitchDeg, rollDeg, alert, labelP, labelR, yawDeg, latG, lonG,
            )
            InclinometerStyle.DUAL_DIAL -> dualDial.draw(
                pitchDeg, rollDeg, alert, yawDeg, latG, lonG,
            )
            InclinometerStyle.BUBBLE -> bubble.draw(
                pitchDeg, rollDeg, alert, yawDeg, latG, lonG,
            )
        }
        return bitmap
    }

    private fun renderLadder(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitchThresholdDeg: Float?,
        maxRollThresholdDeg: Float?,
        labelPitchDeg: Float,
        labelRollDeg: Float,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
    ) {
        val frame = InclinometerSegmentLogic.frame(
            pitchDeg, rollDeg, maxPitchThresholdDeg, maxRollThresholdDeg,
        )
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        ladder.drawRoll(left = true, fill = frame.leftRollFill, rollDeg = frame.rollDeg, cx = cx, cy = cy)
        ladder.drawRoll(left = false, fill = frame.rightRollFill, rollDeg = frame.rollDeg, cx = cx, cy = cy)
        ladder.drawPitch(frame, cx, cy)
        ladder.drawCornerReadouts(labelPitchDeg, labelRollDeg, yawDeg, latG, lonG)
        if (pitchAlert || rollAlert) ladder.drawAlertFrame()
    }

    companion object {
        const val DEFAULT_SIZE_PX = 256
    }
}
