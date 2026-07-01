package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    isPortraitLayout: Boolean = false,
) {
    val trailBuffer = remember { GBallTrailBuffer() }
    val ball = remember(pitchDeg, rollDeg, latG, lonG, mode, displayRotation, isPortraitLayout) {
        ballForMode(mode, pitchDeg, rollDeg, latG, lonG, displayRotation, isPortraitLayout)
    }
    val showTrail = recording
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
        GaugeDisplayRotation.mapAttitude(peakPitchDeg, peakRollDeg, displayRotation, isPortraitLayout)
    } else {
        null
    }
    var showDetail by remember { mutableStateOf(false) }
    val calibrateLabel = stringResource(R.string.gauge_calibrate)
    val gaugeAlert = pitchAlertActive || rollAlertActive || latGAlertActive
    val latGText = GaugeLogic.formatWholeG(latG)
    val lonGText = GaugeLogic.formatWholeG(lonG)

    Box(
        modifier = modifier
            .padding(SpacingSm)
            .then(
                if (gaugeAlert) Modifier.border(3.dp, GaugeRed, CircleShape) else Modifier,
            )
            .clickable { showDetail = true }
            .testTag("attitude_g_meter")
            .semantics { contentDescription = calibrateLabel },
    ) {
        AttitudeGMeterCanvas(
            ball = ball,
            mode = mode,
            trailPoints = trailPoints,
            peakBall = peakBall,
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            latG = latG,
            lonG = lonG,
            pitchAlertActive = pitchAlertActive,
            rollAlertActive = rollAlertActive,
            latGAlertActive = latGAlertActive,
            showTrail = showTrail,
            isPortraitLayout = isPortraitLayout,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (showDetail) {
        ModalBottomSheet(
            onDismissRequest = { showDetail = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = { WindowInsets.navigationBars },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(SpacingMd),
                verticalArrangement = Arrangement.spacedBy(SpacingSm),
            ) {
                Text(stringResource(R.string.gauge_detail_title), color = GaugeYellow)
                Text(stringResource(R.string.gauge_pitch, GaugeLogic.formatWholeDegrees(pitchDeg)), color = GaugeScaleWhite)
                Text(stringResource(R.string.gauge_roll, GaugeLogic.formatWholeDegrees(rollDeg)), color = GaugeScaleWhite)
                Text(stringResource(R.string.gauge_lat_g, latGText), color = GaugeScaleWhite)
                Text(stringResource(R.string.gauge_lon_g, lonGText), color = GaugeScaleWhite)
                val magnitude = hypot(latG.toDouble(), lonG.toDouble()).toFloat()
                Text(stringResource(R.string.gauge_g_magnitude, magnitude.roundToInt()), color = GaugeScaleWhite)
                if (showPeakHold) {
                    Text(
                        stringResource(R.string.gauge_peak_pitch, GaugeLogic.formatWholeDegrees(peakPitchDeg)),
                        color = GaugeScaleWhite,
                    )
                    Text(
                        stringResource(R.string.gauge_peak_roll, GaugeLogic.formatWholeDegrees(peakRollDeg)),
                        color = GaugeScaleWhite,
                    )
                }
                Button(
                    onClick = {
                        trailBuffer.clear()
                        trailPoints = emptyList()
                        onCalibrate()
                    },
                    modifier = Modifier.testTag("gauge_calibrate"),
                ) {
                    Text(calibrateLabel)
                }
            }
        }
    }
}

internal fun ballForMode(
    mode: AttitudeGaugeMode,
    pitchDeg: Float,
    rollDeg: Float,
    latG: Float,
    lonG: Float,
    displayRotation: Int,
    isPortraitLayout: Boolean = false,
): BallPosition = when (mode) {
    AttitudeGaugeMode.ATTITUDE ->
        GaugeDisplayRotation.mapAttitude(pitchDeg, rollDeg, displayRotation, isPortraitLayout)
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
        val rotated = GaugeDisplayRotation.rotateBall(combined, displayRotation)
        if (isPortraitLayout) GaugeDisplayRotation.rotate90Clockwise(rotated) else rotated
    }
}
