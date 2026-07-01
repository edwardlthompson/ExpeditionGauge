package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun VehicleTopDownDiagram(
    modifier: Modifier = Modifier,
    highContrast: Boolean = false,
) {
    val stroke = if (highContrast) 2.5f else 2f
    val outline = if (highContrast) GaugeScaleWhite else GaugeScaleWhite.copy(alpha = 0.9f)
    val fillAlpha = if (highContrast) 0.18f else 0.12f
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val body = sedanBodyPath(w, h)
        drawPath(body, outline.copy(alpha = fillAlpha), style = Fill)
        drawPath(body, outline, style = Stroke(width = stroke))
        val cabin = cabinGlassPath(w, h)
        drawPath(cabin, outline.copy(alpha = fillAlpha + 0.1f), style = Fill)
        drawPath(cabin, outline.copy(alpha = 0.55f), style = Stroke(width = stroke * 0.75f))
        drawWheel(w * 0.17f, h * 0.17f, w * 0.13f, h * 0.11f, outline, stroke, fillAlpha)
        drawWheel(w * 0.70f, h * 0.17f, w * 0.13f, h * 0.11f, outline, stroke, fillAlpha)
        drawWheel(w * 0.17f, h * 0.72f, w * 0.13f, h * 0.11f, outline, stroke, fillAlpha)
        drawWheel(w * 0.70f, h * 0.72f, w * 0.13f, h * 0.11f, outline, stroke, fillAlpha)
        drawLine(
            color = outline.copy(alpha = 0.45f),
            start = Offset(cx, h * 0.14f),
            end = Offset(cx, h * 0.88f),
            strokeWidth = stroke * 0.5f,
        )
    }
}

private fun sedanBodyPath(w: Float, h: Float): Path = Path().apply {
    val cx = w / 2f
    moveTo(cx, h * 0.06f)
    cubicTo(w * 0.42f, h * 0.06f, w * 0.26f, h * 0.12f, w * 0.22f, h * 0.22f)
    lineTo(w * 0.18f, h * 0.48f)
    cubicTo(w * 0.16f, h * 0.62f, w * 0.18f, h * 0.78f, w * 0.24f, h * 0.88f)
    cubicTo(w * 0.30f, h * 0.96f, w * 0.38f, h * 0.98f, cx, h * 0.98f)
    cubicTo(w * 0.62f, h * 0.98f, w * 0.70f, h * 0.96f, w * 0.76f, h * 0.88f)
    cubicTo(w * 0.82f, h * 0.78f, w * 0.84f, h * 0.62f, w * 0.82f, h * 0.48f)
    lineTo(w * 0.78f, h * 0.22f)
    cubicTo(w * 0.74f, h * 0.12f, w * 0.58f, h * 0.06f, cx, h * 0.06f)
    close()
}

private fun cabinGlassPath(w: Float, h: Float): Path = Path().apply {
    val cx = w / 2f
    moveTo(cx, h * 0.20f)
    lineTo(w * 0.32f, h * 0.26f)
    lineTo(w * 0.30f, h * 0.72f)
    lineTo(w * 0.36f, h * 0.80f)
    lineTo(cx, h * 0.82f)
    lineTo(w * 0.64f, h * 0.80f)
    lineTo(w * 0.70f, h * 0.72f)
    lineTo(w * 0.68f, h * 0.26f)
    close()
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWheel(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    outline: androidx.compose.ui.graphics.Color,
    stroke: Float,
    fillAlpha: Float,
) {
    val origin = Offset(left, top)
    val wheelSize = Size(width, height)
    drawRoundRect(
        color = outline.copy(alpha = fillAlpha + 0.14f),
        topLeft = origin,
        size = wheelSize,
        cornerRadius = CornerRadius(height * 0.35f, height * 0.35f),
    )
    drawRoundRect(
        color = outline,
        topLeft = origin,
        size = wheelSize,
        cornerRadius = CornerRadius(height * 0.35f, height * 0.35f),
        style = Stroke(width = stroke),
    )
}
