package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackDriftOverlayLeft
import dev.foss.expeditiongauge.ui.theme.PlaybackDriftOverlayRight
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun VehicleDriftOverlay(
    beta: Float?,
    bodyYawDeg: Float?,
    velocityHeadingDeg: Float?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!enabled) return
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.08f
        val drift = beta ?: 0f
        val body = bodyYawDeg ?: 0f
        val velocity = velocityHeadingDeg ?: body
        val wedgeSweep = drift.coerceIn(-45f, 45f)
        val tailLength = (kotlin.math.abs(drift) / 45f).coerceIn(0.15f, 1f) * radius * 2.5f

        rotate(body, center) {
            drawRect(
                color = GaugeScaleWhite.copy(alpha = 0.85f),
                topLeft = Offset(center.x - radius * 0.35f, center.y - radius * 0.6f),
                size = androidx.compose.ui.geometry.Size(radius * 0.7f, radius * 1.2f),
            )
            drawLine(
                color = GaugeYellow,
                start = center,
                end = Offset(center.x, center.y - radius * 1.4f),
                strokeWidth = 4f,
            )
        }

        rotate(velocity, center) {
            drawLine(
                color = Color.Cyan,
                start = center,
                end = Offset(center.x, center.y - radius * 1.2f),
                strokeWidth = 3f,
            )
        }

        if (kotlin.math.abs(wedgeSweep) > 1f) {
            val wedgePath = Path().apply {
                moveTo(center.x, center.y)
                val startRad = Math.toRadians((velocity - 90f).toDouble())
                val endRad = Math.toRadians((velocity - 90f + wedgeSweep).toDouble())
                lineTo(
                    center.x + (cos(startRad) * tailLength).toFloat(),
                    center.y + (sin(startRad) * tailLength).toFloat(),
                )
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        center.x - tailLength,
                        center.y - tailLength,
                        center.x + tailLength,
                        center.y + tailLength,
                    ),
                    startAngleDegrees = (velocity - 90f),
                    sweepAngleDegrees = wedgeSweep,
                    forceMoveTo = false,
                )
                close()
            }
            drawPath(
                wedgePath,
                color = if (wedgeSweep > 0) PlaybackDriftOverlayLeft else PlaybackDriftOverlayRight,
            )
        }
    }
}
