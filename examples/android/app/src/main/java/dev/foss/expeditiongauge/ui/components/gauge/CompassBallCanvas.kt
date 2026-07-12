package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import dev.foss.expeditiongauge.gauge.CompassBallLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

private const val MeridianCount = 8
private const val ParallelCount = 5
private const val SegsPerRing = 24

@Composable
fun CompassBallCanvas(
    pitchDeg: Float,
    rollDeg: Float,
    yawDeg: Float,
    cardinalsTrusted: Boolean,
    modifier: Modifier = Modifier,
) {
    val meridianLons = remember {
        FloatArray(MeridianCount) { i -> i * (360f / MeridianCount) }
    }
    val parallelLats = remember {
        // include equator (0); skip poles
        floatArrayOf(-60f, -30f, 0f, 30f, 60f).copyOf(ParallelCount)
    }
    val path = remember { Path() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.78f
        val stroke = Stroke(width = 2f)

        fun toScreen(p: CompassBallLogic.ProjectedPoint): Offset =
            Offset(center.x + p.x * radius, center.y - p.y * radius)

        fun depthAlpha(depth: Float): Float {
            // z toward camera is higher when closer in our projection convention
            return (0.25f + (depth + 1f) * 0.35f).coerceIn(0.2f, 0.95f)
        }

        // Outer reference circle
        drawCircle(
            color = GaugeScaleWhite.copy(alpha = 0.35f),
            radius = radius,
            center = center,
            style = stroke,
        )

        // Meridians
        for (lon in meridianLons) {
            path.reset()
            var started = false
            for (i in 0..SegsPerRing) {
                val lat = -90f + 180f * i / SegsPerRing
                val proj = CompassBallLogic.project(
                    CompassBallLogic.spherePoint(lat, lon),
                    pitchDeg, rollDeg, yawDeg,
                )
                if (proj.depth < -0.05f) {
                    started = false
                    continue
                }
                val o = toScreen(proj)
                if (!started) {
                    path.moveTo(o.x, o.y)
                    started = true
                } else {
                    path.lineTo(o.x, o.y)
                }
            }
            drawPath(
                path,
                GaugeYellow.copy(alpha = 0.55f),
                style = stroke,
            )
        }

        // Parallels (equator thicker)
        for (lat in parallelLats) {
            path.reset()
            var started = false
            val isEquator = lat == 0f
            for (i in 0..SegsPerRing) {
                val lon = 360f * i / SegsPerRing
                val proj = CompassBallLogic.project(
                    CompassBallLogic.spherePoint(lat, lon),
                    pitchDeg, rollDeg, yawDeg,
                )
                if (proj.depth < -0.05f) {
                    started = false
                    continue
                }
                val o = toScreen(proj)
                if (!started) {
                    path.moveTo(o.x, o.y)
                    started = true
                } else {
                    path.lineTo(o.x, o.y)
                }
            }
            drawPath(
                path,
                (if (isEquator) GaugeScaleWhite else GaugeYellow)
                    .copy(alpha = if (isEquator) 0.9f else 0.4f),
                style = Stroke(width = if (isEquator) 3f else 1.5f),
            )
        }

        // Cardinals on equator
        val cardAlpha = if (cardinalsTrusted) 1f else 0.45f
        val paint = android.graphics.Paint().apply {
            color = GaugeYellow.copy(alpha = cardAlpha).toArgb()
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = radius * 0.14f
            isAntiAlias = true
            isFakeBoldText = true
        }
        for (label in charArrayOf('N', 'E', 'S', 'W')) {
            val lon = CompassBallLogic.cardinalLonDeg(label)
            val proj = CompassBallLogic.project(
                CompassBallLogic.spherePoint(0f, lon),
                pitchDeg, rollDeg, yawDeg,
            )
            if (proj.depth < -0.1f) continue
            val o = toScreen(proj)
            paint.alpha = (depthAlpha(proj.depth) * cardAlpha * 255f).toInt().coerceIn(40, 255)
            drawContext.canvas.nativeCanvas.drawText(
                if (!cardinalsTrusted && label == 'N') "—" else label.toString(),
                o.x,
                o.y + paint.textSize * 0.35f,
                paint,
            )
        }

        // Fixed vehicle reticle
        val cross = radius * 0.12f
        drawLine(GaugeScaleWhite, Offset(center.x - cross, center.y), Offset(center.x + cross, center.y), 3f)
        drawLine(GaugeScaleWhite, Offset(center.x, center.y - cross), Offset(center.x, center.y + cross), 3f)
        drawCircle(Color.Transparent, radius = 6f, center = center, style = Stroke(width = 2f))
        drawCircle(GaugeScaleWhite, radius = 6f, center = center, style = Stroke(width = 2f))
    }
}
