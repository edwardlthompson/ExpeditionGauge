package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.hypot

/**
 * AA ball-in-ring G-meter (vehicle pitch/roll). Corner readouts match inclinometer
 * (TL pitch · TR roll · BL yaw · BR g). No trail / peak ghost on v1.
 */
class GMeterBitmapRenderer(
    private val sizePx: Int = DEFAULT_SIZE_PX,
) {
    private val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val kit = InclinometerPaintKit(sizePx)

    fun render(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
        darkBackground: Boolean = true,
    ): Bitmap {
        val bg = if (darkBackground) InclinometerColor.BACKGROUND else InclinometerColor.BACKGROUND_LIGHT
        canvas.drawColor(bg)
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val radius = sizePx / 2f * 0.78f
        val stroke = sizePx * 0.012f

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
        }
        listOf(10f to 0xFF33FF33.toInt(), 20f to 0xFFFFDD00.toInt(), 30f to 0xFFFF3333.toInt()).forEach { (deg, color) ->
            ringPaint.color = color
            canvas.drawCircle(cx, cy, radius * AaBallLogic.ringRadiusFraction(deg), ringPaint)
        }

        val cross = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            strokeWidth = sizePx * 0.01f
        }
        canvas.drawLine(cx - radius, cy, cx + radius, cy, cross)
        canvas.drawLine(cx, cy - radius, cx, cy + radius, cross)

        val ball = AaBallLogic.mapPitchRoll(pitchDeg, rollDeg)
        val dist = hypot(ball.normalizedX.toDouble(), ball.normalizedY.toDouble()).toFloat()
        val alert = pitchAlert || rollAlert
        val fill = if (alert) 0xFFFF3333.toInt() else InclinometerColor.argbForNormalized(dist)
        val bx = cx + ball.normalizedX * radius
        val by = cy + ball.normalizedY * radius
        val outerR = sizePx * 0.045f
        val innerR = sizePx * 0.018f
        canvas.drawCircle(bx, by, outerR + sizePx * 0.006f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xD9000000.toInt()
        })
        canvas.drawCircle(bx, by, outerR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fill })
        canvas.drawCircle(bx, by, innerR, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE8E8E8.toInt()
        })

        InclinometerCornerReadouts.draw(
            canvas, sizePx, kit, pitchDeg, rollDeg, yawDeg, latG, lonG,
        )
        return bitmap
    }

    companion object {
        const val DEFAULT_SIZE_PX = 280
    }
}
