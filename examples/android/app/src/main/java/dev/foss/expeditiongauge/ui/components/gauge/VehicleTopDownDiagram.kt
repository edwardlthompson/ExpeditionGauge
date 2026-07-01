package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun VehicleTopDownDiagram(
    modifier: Modifier = Modifier,
    highContrast: Boolean = false,
) {
    val stroke = if (highContrast) 2.5f else 2f
    val outline = if (highContrast) GaugeScaleWhite else GaugeScaleWhite.copy(alpha = 0.85f)
    val fillAlpha = if (highContrast) 0.22f else 0.14f
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bodyLeft = w * 0.18f
        val bodyTop = h * 0.12f
        val bodyW = w * 0.64f
        val bodyH = h * 0.76f
        drawRoundRect(
            color = outline.copy(alpha = fillAlpha),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(bodyW * 0.12f, bodyW * 0.12f),
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(bodyW * 0.12f, bodyW * 0.12f),
            style = Stroke(width = stroke),
        )
        val cabinLeft = bodyLeft + bodyW * 0.14f
        val cabinTop = bodyTop + bodyH * 0.18f
        val cabinW = bodyW * 0.72f
        val cabinH = bodyH * 0.64f
        drawRoundRect(
            color = outline.copy(alpha = fillAlpha + 0.08f),
            topLeft = Offset(cabinLeft, cabinTop),
            size = Size(cabinW, cabinH),
            cornerRadius = CornerRadius(cabinW * 0.08f, cabinW * 0.08f),
        )
        val wheelW = bodyW * 0.22f
        val wheelH = bodyH * 0.16f
        val wheelRadius = CornerRadius(wheelH * 0.35f, wheelH * 0.35f)
        listOf(
            Offset(bodyLeft - wheelW * 0.35f, bodyTop + bodyH * 0.08f),
            Offset(bodyLeft + bodyW - wheelW * 0.65f, bodyTop + bodyH * 0.08f),
            Offset(bodyLeft - wheelW * 0.35f, bodyTop + bodyH - wheelH - bodyH * 0.08f),
            Offset(bodyLeft + bodyW - wheelW * 0.65f, bodyTop + bodyH - wheelH - bodyH * 0.08f),
        ).forEach { origin ->
            drawRoundRect(
                color = outline.copy(alpha = fillAlpha + 0.12f),
                topLeft = origin,
                size = Size(wheelW, wheelH),
                cornerRadius = wheelRadius,
            )
            drawRoundRect(
                color = outline,
                topLeft = origin,
                size = Size(wheelW, wheelH),
                cornerRadius = wheelRadius,
                style = Stroke(width = stroke),
            )
        }
    }
}
