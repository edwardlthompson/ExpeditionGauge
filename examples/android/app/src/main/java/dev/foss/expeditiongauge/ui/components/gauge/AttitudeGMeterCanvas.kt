package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.gauge.AttitudeBallLogic
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.BallPosition
import dev.foss.expeditiongauge.gauge.GForceBallLogic
import dev.foss.expeditiongauge.gauge.GmeterBallColor
import dev.foss.expeditiongauge.gauge.GmeterEdgeNumerals
import dev.foss.expeditiongauge.ui.theme.GaugeBall
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun AttitudeGMeterCanvas(
    ball: BallPosition,
    mode: AttitudeGaugeMode,
    trailPoints: List<Pair<Float, Float>>,
    peakBall: BallPosition?,
    pitchDeg: Float,
    rollDeg: Float,
    latG: Float,
    lonG: Float,
    pitchAlertActive: Boolean,
    rollAlertActive: Boolean,
    latGAlertActive: Boolean,
    showTrail: Boolean,
    isPortraitLayout: Boolean = false,
    highContrast: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val alertActive = pitchAlertActive || rollAlertActive || latGAlertActive
    val rollColor = if (rollAlertActive) GaugeRed else GaugeScaleWhite
    val pitchColor = if (pitchAlertActive) GaugeRed else GaugeScaleWhite
    val showAttitudeEdges = mode == AttitudeGaugeMode.ATTITUDE || mode == AttitudeGaugeMode.HYBRID
    val showGForceEdges = mode == AttitudeGaugeMode.G_FORCE || mode == AttitudeGaugeMode.HYBRID
    val ringAlpha = 1f
    val ringStroke = if (highContrast) 5f else 4f
    val crosshairWidth = if (highContrast) 3.5f else 3f
    val edgeStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
    val ballFill = GmeterBallColor.fillColor(ball, alertActive)
    val ballOuterRadius = if (highContrast) 13f else 12f
    val ballInnerRadius = if (highContrast) 5.5f else 5f

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.78f
            if (showAttitudeEdges) {
                drawAttitudeRings(center, radius, ringAlpha, ringStroke)
            }
            if (showGForceEdges) {
                drawGForceRings(center, radius, ringAlpha, ringStroke)
            }
            drawLine(
                GaugeScaleWhite,
                Offset(center.x - radius, center.y),
                Offset(center.x + radius, center.y),
                crosshairWidth,
            )
            drawLine(
                GaugeScaleWhite,
                Offset(center.x, center.y - radius),
                Offset(center.x, center.y + radius),
                crosshairWidth,
            )
            if (showTrail && trailPoints.size > 1) {
                for (index in 1 until trailPoints.size) {
                    val (prevX, prevY) = trailPoints[index - 1]
                    val (nx, ny) = trailPoints[index]
                    val progress = index.toFloat() / trailPoints.size
                    val dist = hypot(nx.toDouble(), ny.toDouble()).toFloat()
                    val trailColor = GmeterBallColor.colorForDistance(dist)
                        .copy(alpha = 0.25f + progress * 0.55f)
                    drawLine(
                        color = trailColor,
                        start = Offset(center.x + prevX * radius, center.y + prevY * radius),
                        end = Offset(center.x + nx * radius, center.y + ny * radius),
                        strokeWidth = if (highContrast) 4f else 3f,
                    )
                }
            }
            if (showTrail && trailPoints.isNotEmpty()) {
                trailPoints.forEachIndexed { index, (nx, ny) ->
                    val progress = (index + 1).toFloat() / trailPoints.size
                    val dist = hypot(nx.toDouble(), ny.toDouble()).toFloat()
                    val trailColor = GmeterBallColor.colorForDistance(dist)
                        .copy(alpha = 0.35f + progress * 0.55f)
                    val trailCenter = Offset(center.x + nx * radius, center.y + ny * radius)
                    drawCircle(color = trailColor, radius = if (highContrast) 5f else 4f, center = trailCenter)
                }
            }
            val ballOffset = Offset(center.x + ball.normalizedX * radius, center.y + ball.normalizedY * radius)
            drawCircle(color = Color.Black.copy(alpha = 0.85f), radius = ballOuterRadius + 1.5f, center = ballOffset)
            drawCircle(color = ballFill, radius = ballOuterRadius, center = ballOffset)
            drawCircle(color = GaugeBall, radius = ballInnerRadius, center = ballOffset)
            peakBall?.let { peak ->
                val peakOffset = Offset(
                    center.x + peak.normalizedX * radius,
                    center.y + peak.normalizedY * radius,
                )
                drawCircle(
                    color = GaugeScaleWhite.copy(alpha = 0.55f),
                    radius = 8f,
                    center = peakOffset,
                    style = Stroke(width = 2.5f),
                )
            }
        }
        if (showAttitudeEdges) {
            if (isPortraitLayout) {
                Text(
                    text = GmeterEdgeNumerals.topRollReadout(rollDeg, ball.normalizedY),
                    color = rollColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.bottomRollReadout(rollDeg, ball.normalizedY),
                    color = rollColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.leftPitchReadout(pitchDeg, ball.normalizedX),
                    color = pitchColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.rightPitchReadout(pitchDeg, ball.normalizedX),
                    color = pitchColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 2.dp),
                )
            } else {
                Text(
                    text = GmeterEdgeNumerals.topPitchReadout(pitchDeg, ball.normalizedY),
                    color = pitchColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.bottomPitchReadout(pitchDeg, ball.normalizedY),
                    color = pitchColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.leftRollReadout(rollDeg, ball.normalizedX),
                    color = rollColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.rightRollReadout(rollDeg, ball.normalizedX),
                    color = rollColor,
                    style = edgeStyle,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 2.dp),
                )
            }
        }
        if (showGForceEdges && !showAttitudeEdges) {
            Text(
                text = GmeterEdgeNumerals.topLonGReadout(lonG, ball.normalizedY),
                color = GaugeScaleWhite,
                style = edgeStyle,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
            )
            Text(
                text = GmeterEdgeNumerals.bottomLonGReadout(lonG, ball.normalizedY),
                color = GaugeScaleWhite,
                style = edgeStyle,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAttitudeRings(
    center: Offset,
    radius: Float,
    alpha: Float,
    strokeWidth: Float,
) {
    listOf(
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_10_DEG to GaugeGreen,
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_20_DEG to GaugeYellow,
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_30_DEG to GaugeRed,
    ).forEach { (ringDeg, ringColor) ->
        val ringRadius = radius * AttitudeBallLogic.ringRadiusFraction(ringDeg)
        drawCircle(ringColor.copy(alpha = alpha), ringRadius, center, style = Stroke(strokeWidth))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGForceRings(
    center: Offset,
    radius: Float,
    alpha: Float,
    strokeWidth: Float,
) {
    listOf(
        GForceBallLogic.RING_05_G to GaugeGreen,
        GForceBallLogic.RING_10_G to GaugeYellow,
        GForceBallLogic.RING_15_G to GaugeRed,
    ).forEach { (ringG, ringColor) ->
        val ringRadius = radius * GForceBallLogic.ringRadiusFraction(ringG)
        drawCircle(ringColor.copy(alpha = alpha), ringRadius, center, style = Stroke(strokeWidth))
    }
}

private fun hypot(x: Double, y: Double): Double = kotlin.math.hypot(x, y)
