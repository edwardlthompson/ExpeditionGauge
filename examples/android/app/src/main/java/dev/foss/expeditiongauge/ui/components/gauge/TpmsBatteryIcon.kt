package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun TpmsBatteryIcon(
    batteryPct: Int?,
    modifier: Modifier = Modifier,
    highContrast: Boolean = false,
) {
    val stroke = if (highContrast) 2f else 1.5f
    Canvas(modifier.size(width = 26.dp, height = 14.dp)) {
        val outline = if (highContrast) GaugeScaleWhite else GaugeScaleWhite.copy(alpha = 0.9f)
        val bodyW = size.width * 0.82f
        val bodyH = size.height * 0.72f
        val top = (size.height - bodyH) / 2f
        drawRoundRect(
            color = outline,
            topLeft = Offset(0f, top),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(width = stroke),
        )
        val nubW = size.width - bodyW
        drawRoundRect(
            color = outline,
            topLeft = Offset(bodyW, top + bodyH * 0.25f),
            size = Size(nubW, bodyH * 0.5f),
            cornerRadius = CornerRadius(1f, 1f),
        )
        val pct = batteryPct?.coerceIn(0, 100)
        val fillColor = when {
            pct == null -> Color.Transparent
            pct <= 15 -> GaugeRed
            pct <= 35 -> GaugeYellow
            else -> if (highContrast) GaugeScaleWhite else GaugeScaleWhite.copy(alpha = 0.85f)
        }
        if (pct != null && pct > 0) {
            val inset = stroke + 1.5f
            val fillW = (bodyW - inset * 2f) * (pct / 100f)
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(inset, top + inset),
                size = Size(fillW.coerceAtLeast(2f), bodyH - inset * 2f),
                cornerRadius = CornerRadius(1f, 1f),
            )
        }
    }
}
