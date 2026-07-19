package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

/** AA wireframe 3D compass sphere bitmap. */
class CompassBallBitmapRenderer(
    private val sizePx: Int = DEFAULT_SIZE_PX,
) {
    private val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val path = Path()

    fun render(
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
        cardinalsTrusted: Boolean,
        darkBackground: Boolean = true,
    ): Bitmap {
        val bg = if (darkBackground) InclinometerColor.BACKGROUND else InclinometerColor.BACKGROUND_LIGHT
        canvas.drawColor(bg)
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val radius = sizePx / 2f * 0.78f

        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = sizePx * 0.008f
            color = 0x59FFFFFF.toInt()
        }
        canvas.drawCircle(cx, cy, radius, stroke)

        fun toScreen(p: CompassBallLogic.ProjectedPoint): Pair<Float, Float> =
            (cx + p.x * radius) to (cy - p.y * radius)

        fun depthAlpha(depth: Float): Float =
            (0.25f + (depth + 1f) * 0.35f).coerceIn(0.2f, 0.95f)

        val meridianLons = FloatArray(MERIDIAN_COUNT) { i -> i * (360f / MERIDIAN_COUNT) }
        val parallelLats = floatArrayOf(-60f, -30f, 0f, 30f, 60f)
        val yellow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = sizePx * 0.007f
        }

        for (lon in meridianLons) {
            path.reset()
            var started = false
            for (i in 0..SEGS) {
                val lat = -90f + 180f * i / SEGS
                val proj = CompassBallLogic.project(
                    CompassBallLogic.spherePoint(lat, lon),
                    pitchDeg, rollDeg, yawDeg,
                )
                if (proj.depth < -0.05f) {
                    started = false
                    continue
                }
                val (sx, sy) = toScreen(proj)
                if (!started) {
                    path.moveTo(sx, sy)
                    started = true
                } else {
                    path.lineTo(sx, sy)
                }
            }
            yellow.color = 0x8CFFDD00.toInt()
            canvas.drawPath(path, yellow)
        }

        for (lat in parallelLats) {
            path.reset()
            var started = false
            val isEquator = lat == 0f
            for (i in 0..SEGS) {
                val lon = 360f * i / SEGS
                val proj = CompassBallLogic.project(
                    CompassBallLogic.spherePoint(lat, lon),
                    pitchDeg, rollDeg, yawDeg,
                )
                if (proj.depth < -0.05f) {
                    started = false
                    continue
                }
                val (sx, sy) = toScreen(proj)
                if (!started) {
                    path.moveTo(sx, sy)
                    started = true
                } else {
                    path.lineTo(sx, sy)
                }
            }
            yellow.strokeWidth = if (isEquator) sizePx * 0.012f else sizePx * 0.006f
            yellow.color = if (isEquator) 0xE6FFFFFF.toInt() else 0x66FFDD00.toInt()
            canvas.drawPath(path, yellow)
        }

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = radius * 0.14f
            isFakeBoldText = true
            color = 0xFFFFDD00.toInt()
        }
        val cardAlpha = if (cardinalsTrusted) 1f else 0.45f
        for (label in charArrayOf('N', 'E', 'S', 'W')) {
            val lon = CompassBallLogic.cardinalLonDeg(label)
            val proj = CompassBallLogic.project(
                CompassBallLogic.spherePoint(0f, lon),
                pitchDeg, rollDeg, yawDeg,
            )
            if (proj.depth < -0.1f) continue
            val (sx, sy) = toScreen(proj)
            cardPaint.alpha = (depthAlpha(proj.depth) * cardAlpha * 255f).toInt().coerceIn(40, 255)
            val text = if (!cardinalsTrusted && label == 'N') "—" else label.toString()
            canvas.drawText(text, sx, sy + cardPaint.textSize * 0.35f, cardPaint)
        }

        val cross = radius * 0.12f
        val reticle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            strokeWidth = sizePx * 0.01f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(cx - cross, cy, cx + cross, cy, reticle)
        canvas.drawLine(cx, cy - cross, cx, cy + cross, reticle)
        canvas.drawCircle(cx, cy, sizePx * 0.022f, reticle)
        return bitmap
    }

    companion object {
        const val DEFAULT_SIZE_PX = 280
        private const val MERIDIAN_COUNT = 8
        private const val SEGS = 24
    }
}
