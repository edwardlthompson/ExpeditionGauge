package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.car.gauge.InclinometerCarIcon
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.gauge.GaugeDisplayRotation
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InclinometerGauge(
    pitchDeg: Float,
    rollDeg: Float,
    onCalibrate: () -> Unit,
    modifier: Modifier = Modifier,
    style: InclinometerStyle = InclinometerStyle.LADDER,
    onCycleStyle: (() -> Unit)? = null,
    onToggleDisplay: (() -> Unit)? = null,
    isPortraitLayout: Boolean = true,
    displayRotation: Int = 0,
    pitchAlertActive: Boolean = false,
    rollAlertActive: Boolean = false,
    maxPitchThresholdDeg: Float? = null,
    maxRollThresholdDeg: Float? = null,
    yawDeg: Float? = null,
    latG: Float? = null,
    lonG: Float? = null,
    gaugeSizeDp: Dp = 180.dp,
) {
    var showSheet by remember { mutableStateOf(false) }
    val alertActive = pitchAlertActive || rollAlertActive
    val (displayPitch, displayRoll) = remember(pitchDeg, rollDeg, isPortraitLayout, displayRotation) {
        GaugeDisplayRotation.mapFusionToInclinometerAxes(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            isPortraitLayout = isPortraitLayout,
            displayRotation = displayRotation,
        )
    }
    val bitmap = remember(
        displayPitch,
        displayRoll,
        pitchDeg,
        rollDeg,
        style,
        pitchAlertActive,
        rollAlertActive,
        maxPitchThresholdDeg,
        maxRollThresholdDeg,
        yawDeg,
        latG,
        lonG,
        gaugeSizeDp,
    ) {
        InclinometerCarIcon.renderBitmap(
            pitchDeg = displayPitch,
            rollDeg = displayRoll,
            style = style,
            pitchAlert = pitchAlertActive,
            rollAlert = rollAlertActive,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
            labelPitchDeg = displayPitch,
            labelRollDeg = displayRoll,
            yawDeg = yawDeg,
            latG = latG,
            lonG = lonG,
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (alertActive) {
                    Modifier.border(3.dp, GaugeRed, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                },
            )
            .attitudeGaugeInteraction(
                onToggleDisplay = {
                    onToggleDisplay?.invoke() ?: run { showSheet = true }
                },
                onLongPressCalibrate = { showSheet = true },
            )
            .semantics {
                contentDescription = buildString {
                    append("Inclinometer pitch ${GaugeLogic.formatSignedDegrees(displayPitch)}, ")
                    append("roll ${GaugeLogic.formatSignedDegrees(displayRoll)}")
                    yawDeg?.let { append(", yaw ${GaugeLogic.formatSignedDegrees(it)}") }
                    latG?.let { append(", latG %.1f".format(it)) }
                    lonG?.let { append(", lonG %.1f".format(it)) }
                    append(", style ${style.name}")
                }
            }
            .testTag("inclinometer_gauge"),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.92f),
        )
    }

    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Text(
                text = stringResource(R.string.gauge_detail_title),
                color = GaugeScaleWhite,
                modifier = Modifier.padding(SpacingMd),
            )
            Text(
                text = buildString {
                    appendLine("P ${GaugeLogic.formatSignedDegrees(displayPitch)}")
                    appendLine("R ${GaugeLogic.formatSignedDegrees(displayRoll)}")
                    yawDeg?.let { appendLine("Y ${GaugeLogic.formatSignedDegrees(it)}") }
                    if (latG != null || lonG != null) {
                        append("G lat ${"%.1f".format(latG ?: 0f)} lon ${"%.1f".format(lonG ?: 0f)}")
                    }
                },
                color = GaugeScaleWhite,
                modifier = Modifier.padding(horizontal = SpacingMd),
            )
            Text(
                text = stringResource(R.string.gauge_inclinometer_style_hint),
                color = GaugeScaleWhite,
                modifier = Modifier.padding(SpacingMd),
            )
            Button(
                onClick = {
                    onCalibrate()
                    showSheet = false
                },
                modifier = Modifier.padding(SpacingMd).testTag("inclinometer_calibrate"),
            ) {
                Text(stringResource(R.string.gauge_calibrate))
            }
            if (onCycleStyle != null) {
                Button(
                    onClick = {
                        onCycleStyle()
                        showSheet = false
                    },
                    modifier = Modifier.padding(SpacingMd).testTag("inclinometer_cycle_style"),
                ) {
                    Text(stringResource(R.string.gauge_inclinometer_style_hint))
                }
            }
            if (onToggleDisplay != null) {
                Button(
                    onClick = {
                        onToggleDisplay()
                        showSheet = false
                    },
                    modifier = Modifier.padding(SpacingMd).testTag("inclinometer_to_gmeter"),
                ) {
                    Text(stringResource(R.string.gauge_switch_to_gmeter))
                }
            }
        }
    }
}
