package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    pitchAlertActive: Boolean = false,
    rollAlertActive: Boolean = false,
    maxPitchThresholdDeg: Float? = null,
    maxRollThresholdDeg: Float? = null,
    gaugeSizeDp: Dp = 180.dp,
) {
    var showSheet by remember { mutableStateOf(false) }
    val alertActive = pitchAlertActive || rollAlertActive
    val bitmap = remember(
        pitchDeg,
        rollDeg,
        pitchAlertActive,
        rollAlertActive,
        maxPitchThresholdDeg,
        maxRollThresholdDeg,
        gaugeSizeDp,
    ) {
        InclinometerCarIcon.renderBitmap(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            pitchAlert = pitchAlertActive,
            rollAlert = rollAlertActive,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
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
            .clickable { showSheet = true }
            .semantics {
                contentDescription = "Inclinometer pitch ${GaugeLogic.formatSignedDegrees(pitchDeg)}, " +
                    "roll ${GaugeLogic.formatSignedDegrees(rollDeg)}"
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
                text = "P ${GaugeLogic.formatSignedDegrees(pitchDeg)}  R ${GaugeLogic.formatSignedDegrees(rollDeg)}",
                color = GaugeScaleWhite,
                modifier = Modifier.padding(horizontal = SpacingMd),
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
        }
    }
}
