package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.CompassBallLogic
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

/**
 * Elite-style wireframe compass ball.
 * Yaw is display-smoothed only (does not write fusion / calibration).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompassBallGauge(
    pitchDeg: Float,
    rollDeg: Float,
    bodyYawDeg: Float?,
    headingDeg: Float,
    onCalibrate: () -> Unit,
    onToggleDisplay: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showSheet by remember { mutableStateOf(false) }
    val resolvedYaw = CompassBallLogic.resolveYawDeg(bodyYawDeg, headingDeg)
    val cardinalsTrusted = resolvedYaw != null
    val targetYaw = resolvedYaw ?: 0f
    var smoothYaw by remember { mutableFloatStateOf(targetYaw) }
    smoothYaw = CompassBallLogic.lerpYawDeg(smoothYaw, targetYaw, 0.2f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .attitudeGaugeInteraction(
                onToggleDisplay = { onToggleDisplay?.invoke() ?: run { showSheet = true } },
                onLongPressCalibrate = { showSheet = true },
            )
            .semantics {
                contentDescription = buildString {
                    append("Compass ball pitch ${GaugeLogic.formatSignedDegrees(pitchDeg)}, ")
                    append("roll ${GaugeLogic.formatSignedDegrees(rollDeg)}")
                    resolvedYaw?.let { append(", yaw ${GaugeLogic.formatSignedDegrees(it)}") }
                }
            }
            .testTag("compass_ball_gauge"),
        contentAlignment = Alignment.Center,
    ) {
        CompassBallCanvas(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            yawDeg = smoothYaw,
            cardinalsTrusted = cardinalsTrusted,
            modifier = Modifier.fillMaxSize(0.92f),
        )
    }

    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Text(
                text = stringResource(R.string.settings_attitude_mode_compass),
                color = GaugeScaleWhite,
                modifier = Modifier.padding(SpacingMd),
            )
            Text(
                text = stringResource(R.string.gauge_toggle_hint),
                color = GaugeScaleWhite,
                modifier = Modifier.padding(horizontal = SpacingMd),
            )
            Button(
                onClick = {
                    onCalibrate()
                    showSheet = false
                },
                modifier = Modifier.padding(SpacingMd).testTag("compass_ball_calibrate"),
            ) {
                Text(stringResource(R.string.gauge_calibrate))
            }
        }
    }
}
