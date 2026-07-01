package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.AttitudeBallLogic
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.BallPosition
import dev.foss.expeditiongauge.gauge.GBallTrailBuffer
import dev.foss.expeditiongauge.gauge.GForceBallLogic
import dev.foss.expeditiongauge.gauge.GaugeDisplayRotation
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.GaugeZone
import dev.foss.expeditiongauge.ui.theme.GaugeBall
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import kotlin.math.hypot
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttitudeGMeterGauge(
    pitchDeg: Float,
    rollDeg: Float,
    onCalibrate: () -> Unit,
    modifier: Modifier = Modifier,
    mode: AttitudeGaugeMode = AttitudeGaugeMode.ATTITUDE,
    latG: Float = 0f,
    lonG: Float = 0f,
    showPeakHold: Boolean = false,
    peakPitchDeg: Float = 0f,
    peakRollDeg: Float = 0f,
    peakAbsPitchDeg: Float = 0f,
    peakAbsRollDeg: Float = 0f,
    pitchAlertActive: Boolean = false,
    rollAlertActive: Boolean = false,
    latGAlertActive: Boolean = false,
    gaugeSizeDp: Dp = 180.dp,
    displayRotation: Int = 0,
    recording: Boolean = false,
) {
    val trailBuffer = remember { GBallTrailBuffer() }
    val ball = remember(pitchDeg, rollDeg, latG, lonG, mode, displayRotation) {
        ballForMode(mode, pitchDeg, rollDeg, latG, lonG, displayRotation)
    }
    val showTrail = mode == AttitudeGaugeMode.G_FORCE || mode == AttitudeGaugeMode.HYBRID
    var trailPoints by remember { mutableStateOf<List<Pair<Float, Float>>>(emptyList()) }
    LaunchedEffect(ball.normalizedX, ball.normalizedY, showTrail) {
        if (showTrail) {
            trailBuffer.add(ball.normalizedX, ball.normalizedY)
            trailPoints = trailBuffer.snapshot()
        }
    }
    LaunchedEffect(recording) {
        if (!recording) {
            trailBuffer.clear()
            trailPoints = emptyList()
        }
    }
    val peakBall = if (showPeakHold && (peakAbsPitchDeg > 0f || peakAbsRollDeg > 0f)) {
        GaugeDisplayRotation.mapAttitude(peakPitchDeg, peakRollDeg, displayRotation)
    } else {
        null
    }
    var showDetail by remember { mutableStateOf(false) }
    val calibrateLabel = stringResource(R.string.gauge_calibrate)
    val attitudeAlert = pitchAlertActive || rollAlertActive
    val gaugeAlert = attitudeAlert || latGAlertActive
    val latGText = GaugeLogic.formatWholeG(latG)
    val lonGText = GaugeLogic.formatWholeG(lonG)

    Column(
        modifier = modifier.padding(SpacingSm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Canvas(
            modifier = Modifier
                .size(gaugeSizeDp)
                .then(
                    if (gaugeAlert) {
                        Modifier.border(3.dp, GaugeRed, CircleShape)
                    } else {
                        Modifier
                    },
                )
                .clickable { showDetail = true }
                .testTag("attitude_g_meter"),
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.9f
            if (mode == AttitudeGaugeMode.ATTITUDE || mode == AttitudeGaugeMode.HYBRID) {
                drawAttitudeRings(center, radius, if (mode == AttitudeGaugeMode.HYBRID) 0.35f else 0.55f)
            }
            if (mode == AttitudeGaugeMode.G_FORCE || mode == AttitudeGaugeMode.HYBRID) {
                drawGForceRings(center, radius, if (mode == AttitudeGaugeMode.HYBRID) 0.55f else 0.55f)
            }
            drawLine(GaugeScaleWhite, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 1f)
            drawLine(GaugeScaleWhite, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 1f)
            if (showTrail && trailPoints.isNotEmpty()) {
                trailPoints.forEachIndexed { index, (nx, ny) ->
                    val alpha = (index + 1).toFloat() / trailPoints.size * 0.45f
                    val trailCenter = Offset(center.x + nx * radius, center.y + ny * radius)
                    drawCircle(
                        color = GaugeBall.copy(alpha = alpha),
                        radius = 4f,
                        center = trailCenter,
                    )
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
        if (mode == AttitudeGaugeMode.ATTITUDE || mode == AttitudeGaugeMode.HYBRID) {
            Text(
                text = stringResource(R.string.gauge_pitch, GaugeLogic.formatWholeDegrees(pitchDeg)),
                color = GaugeScaleWhite,
            )
            Text(
                text = stringResource(R.string.gauge_roll, GaugeLogic.formatWholeDegrees(rollDeg)),
                color = GaugeScaleWhite,
            )
        }
        if (mode == AttitudeGaugeMode.G_FORCE || mode == AttitudeGaugeMode.HYBRID) {
            Text(text = stringResource(R.string.gauge_lat_g, latGText), color = GaugeScaleWhite)
            Text(text = stringResource(R.string.gauge_lon_g, lonGText), color = GaugeScaleWhite)
        }
        Button(
            onClick = {
                trailBuffer.clear()
                trailPoints = emptyList()
                onCalibrate()
            },
            modifier = Modifier
                .testTag("gauge_calibrate")
                .semantics { contentDescription = calibrateLabel },
        ) {
            Text(calibrateLabel)
        }
    }

    if (showDetail) {
        ModalBottomSheet(
            onDismissRequest = { showDetail = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = { WindowInsets.navigationBars },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingMd),
                verticalArrangement = Arrangement.spacedBy(SpacingSm),
            ) {
                Text(stringResource(R.string.gauge_detail_title), color = GaugeYellow)
                Text(
                    stringResource(R.string.gauge_pitch, GaugeLogic.formatWholeDegrees(pitchDeg)),
                    color = GaugeScaleWhite,
                )
                Text(
                    stringResource(R.string.gauge_roll, GaugeLogic.formatWholeDegrees(rollDeg)),
                    color = GaugeScaleWhite,
                )
                Text(stringResource(R.string.gauge_lat_g, latGText), color = GaugeScaleWhite)
                Text(stringResource(R.string.gauge_lon_g, lonGText), color = GaugeScaleWhite)
                val magnitude = hypot(latG.toDouble(), lonG.toDouble()).toFloat()
                Text(
                    stringResource(R.string.gauge_g_magnitude, magnitude.roundToInt()),
                    color = GaugeScaleWhite,
                )
            }
        }
    }
}

private fun ballForMode(
    mode: AttitudeGaugeMode,
    pitchDeg: Float,
    rollDeg: Float,
    latG: Float,
    lonG: Float,
    displayRotation: Int,
): BallPosition = when (mode) {
    AttitudeGaugeMode.ATTITUDE -> GaugeDisplayRotation.mapAttitude(pitchDeg, rollDeg, displayRotation)
    AttitudeGaugeMode.G_FORCE -> GaugeDisplayRotation.mapGForce(latG, lonG, displayRotation)
    AttitudeGaugeMode.HYBRID -> {
        val attitude = AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg)
        val gForce = GForceBallLogic.mapLatLonG(latG, lonG)
        val zone = if (attitude.zone.ordinal >= gForce.zone.ordinal) attitude.zone else gForce.zone
        val combined = BallPosition(
            normalizedX = ((attitude.normalizedX + gForce.normalizedX) / 2f).coerceIn(-1f, 1f),
            normalizedY = ((attitude.normalizedY + gForce.normalizedY) / 2f).coerceIn(-1f, 1f),
            zone = zone,
        )
        GaugeDisplayRotation.rotateBall(combined, displayRotation)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAttitudeRings(
    center: Offset,
    radius: Float,
    alpha: Float,
) {
    listOf(
        GaugeLogic.RING_10_DEG to GaugeGreen,
        GaugeLogic.RING_20_DEG to GaugeYellow,
        GaugeLogic.RING_30_DEG to GaugeRed,
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
