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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.gauge.AttitudeBallLogic
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.BallPosition
import dev.foss.expeditiongauge.gauge.GForceBallLogic
import dev.foss.expeditiongauge.gauge.GmeterEdgeNumerals
import dev.foss.expeditiongauge.gauge.GaugeZone
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
    modifier: Modifier = Modifier,
) {
    val rollColor = if (rollAlertActive) GaugeRed else GaugeScaleWhite
    val pitchColor = if (pitchAlertActive) GaugeRed else GaugeScaleWhite
    val showAttitudeEdges = mode == AttitudeGaugeMode.ATTITUDE || mode == AttitudeGaugeMode.HYBRID
    val showGForceEdges = mode == AttitudeGaugeMode.G_FORCE || mode == AttitudeGaugeMode.HYBRID

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.78f
            if (showAttitudeEdges) {
                drawAttitudeRings(center, radius, if (mode == AttitudeGaugeMode.HYBRID) 0.35f else 0.55f)
            }
            if (showGForceEdges) {
                drawGForceRings(center, radius, if (mode == AttitudeGaugeMode.HYBRID) 0.55f else 0.55f)
            }
            drawLine(GaugeScaleWhite, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 1f)
            drawLine(GaugeScaleWhite, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 1f)
            if (showTrail && trailPoints.isNotEmpty()) {
                trailPoints.forEachIndexed { index, (nx, ny) ->
                    val alpha = (index + 1).toFloat() / trailPoints.size * 0.45f
                    val trailCenter = Offset(center.x + nx * radius, center.y + ny * radius)
                    drawCircle(color = GaugeBall.copy(alpha = alpha), radius = 4f, center = trailCenter)
                }
            }
            val ballColor = when {
                pitchAlertActive || rollAlertActive || latGAlertActive -> GaugeRed
                ball.zone == GaugeZone.Safe -> GaugeGreen
                ball.zone == GaugeZone.Caution -> GaugeYellow
                else -> GaugeRed
            }
            val ballOffset = Offset(center.x + ball.normalizedX * radius, center.y + ball.normalizedY * radius)
            drawCircle(color = ballColor, radius = 10f, center = ballOffset)
            drawCircle(color = GaugeBall, radius = 6f, center = ballOffset)
            peakBall?.let { peak ->
                val peakOffset = Offset(
                    center.x + peak.normalizedX * radius,
                    center.y + peak.normalizedY * radius,
                )
                drawCircle(
                    color = GaugeScaleWhite.copy(alpha = 0.45f),
                    radius = 7f,
                    center = peakOffset,
                    style = Stroke(width = 2f),
                )
            }
        }
        if (showAttitudeEdges) {
            if (isPortraitLayout) {
                Text(
                    text = GmeterEdgeNumerals.topRollReadout(rollDeg, ball.normalizedY),
                    color = rollColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.bottomRollReadout(rollDeg, ball.normalizedY),
                    color = rollColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.leftPitchReadout(pitchDeg, ball.normalizedX),
                    color = pitchColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.rightPitchReadout(pitchDeg, ball.normalizedX),
                    color = pitchColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 2.dp),
                )
            } else {
                Text(
                    text = GmeterEdgeNumerals.topPitchReadout(pitchDeg, ball.normalizedY),
                    color = pitchColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.bottomPitchReadout(pitchDeg, ball.normalizedY),
                    color = pitchColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.leftRollReadout(rollDeg, ball.normalizedX),
                    color = rollColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 2.dp),
                )
                Text(
                    text = GmeterEdgeNumerals.rightRollReadout(rollDeg, ball.normalizedX),
                    color = rollColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 2.dp),
                )
            }
        }
        if (showGForceEdges && !showAttitudeEdges) {
            Text(
                text = GmeterEdgeNumerals.topLonGReadout(lonG, ball.normalizedY),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp),
            )
            Text(
                text = GmeterEdgeNumerals.bottomLonGReadout(lonG, ball.normalizedY),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAttitudeRings(
    center: Offset,
    radius: Float,
    alpha: Float,
) {
    listOf(
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_10_DEG to GaugeGreen,
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_20_DEG to GaugeYellow,
        dev.foss.expeditiongauge.gauge.GaugeLogic.RING_30_DEG to GaugeRed,
    ).forEach { (ringDeg, ringColor) ->
        val ringRadius = radius * AttitudeBallLogic.ringRadiusFraction(ringDeg)
        drawCircle(ringColor.copy(alpha = alpha), ringRadius, center, style = Stroke(2.5f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGForceRings(
    center: Offset,
    radius: Float,
    alpha: Float,
) {
    listOf(
        GForceBallLogic.RING_05_G to GaugeGreen,
        GForceBallLogic.RING_10_G to GaugeYellow,
        GForceBallLogic.RING_15_G to GaugeRed,
    ).forEach { (ringG, ringColor) ->
        val ringRadius = radius * GForceBallLogic.ringRadiusFraction(ringG)
        drawCircle(ringColor.copy(alpha = alpha), ringRadius, center, style = Stroke(2.5f))
    }
}
