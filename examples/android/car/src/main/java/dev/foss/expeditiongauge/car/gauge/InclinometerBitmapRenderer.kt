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
    ): Bitmap {
        canvas.drawColor(InclinometerColor.BACKGROUND)
        val alert = pitchAlert || rollAlert
        val labelP = labelPitchDeg ?: pitchDeg
        val labelR = labelRollDeg ?: rollDeg
        when (style) {
            InclinometerStyle.LADDER -> renderLadder(
                pitchDeg, rollDeg, pitchAlert, rollAlert,
                maxPitchThresholdDeg, maxRollThresholdDeg,
                labelP, labelR, yawDeg,
            )
            InclinometerStyle.HORIZON -> horizon.draw(
                pitchDeg, rollDeg, alert, labelP, labelR, yawDeg,
            )
            InclinometerStyle.DUAL_DIAL -> dualDial.draw(pitchDeg, rollDeg, alert, yawDeg)
            InclinometerStyle.BUBBLE -> bubble.draw(pitchDeg, rollDeg, alert, yawDeg)
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
    ) {
        val frame = InclinometerSegmentLogic.frame(
            pitchDeg, rollDeg, maxPitchThresholdDeg, maxRollThresholdDeg,
        )
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        ladder.drawRoll(left = true, fill = frame.leftRollFill, rollDeg = frame.rollDeg, cx = cx, cy = cy)
        ladder.drawRoll(left = false, fill = frame.rightRollFill, rollDeg = frame.rollDeg, cx = cx, cy = cy)
        ladder.drawPitch(frame, cx, cy)
        ladder.drawReadouts(labelPitchDeg, labelRollDeg, cx, yawDeg)
        if (pitchAlert || rollAlert) ladder.drawAlertFrame()
    }

    companion object {
        const val DEFAULT_SIZE_PX = 256
    }
}
