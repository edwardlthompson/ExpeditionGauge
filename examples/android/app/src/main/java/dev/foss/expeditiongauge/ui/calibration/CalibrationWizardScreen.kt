package dev.foss.expeditiongauge.ui.calibration

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.calibration.CalibrationWizardStep
import dev.foss.expeditiongauge.calibration.CalibrationWizardStore
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.launch

@Composable
fun CalibrationWizardScreen(
    wizardStore: CalibrationWizardStore,
    telemetry: TelemetrySnapshot,
    imuSessionCount: Int,
    onImuManage: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val steps = CalibrationWizardStep.entries
    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState())
            .testTag("calibration_wizard"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.calibration_wizard_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.calibration_wizard_step, stepIndex + 1, steps.size),
            color = GaugeScaleWhite,
            modifier = Modifier.testTag("calibration_wizard_step_label"),
        )
        when (step) {
            CalibrationWizardStep.Mount -> WizardMountStep()
            CalibrationWizardStep.Level -> Text(
                text = stringResource(R.string.calibration_wizard_level),
                color = GaugeScaleWhite,
            )
            CalibrationWizardStep.ImuCorners -> Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
                Text(
                    text = stringResource(R.string.calibration_wizard_imu, imuSessionCount),
                    color = GaugeScaleWhite,
                )
                Button(onClick = onImuManage, modifier = Modifier.testTag("calibration_wizard_imu_manage")) {
                    Text(stringResource(R.string.imu_management_title))
                }
            }
            CalibrationWizardStep.Figure8 -> Text(
                text = stringResource(R.string.calibration_wizard_figure8),
                color = GaugeScaleWhite,
            )
            CalibrationWizardStep.TestDrive -> WizardTestDriveStep(telemetry = telemetry)
        }
        RowNav(
            stepIndex = stepIndex,
            lastIndex = steps.lastIndex,
            onBackStep = { if (stepIndex > 0) stepIndex-- },
            onNextStep = {
                if (stepIndex < steps.lastIndex) {
                    stepIndex++
                } else {
                    scope.launch {
                        wizardStore.markCompleted()
                        onBack()
                    }
                }
            },
            onFinish = stepIndex == steps.lastIndex,
        )
        Button(onClick = onBack, modifier = Modifier.testTag("calibration_wizard_close")) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@Composable
private fun WizardMountStep() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(2.dp, GaugeYellow)
            .padding(SpacingMd)
            .testTag("calibration_wizard_mount"),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.calibration_mount_diagram),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
    Text(text = stringResource(R.string.calibration_tips_intro), color = GaugeScaleWhite)
}

@Composable
private fun WizardTestDriveStep(telemetry: TelemetrySnapshot) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.calibration_wizard_test_drive), color = GaugeScaleWhite)
        Text(
            text = stringResource(
                R.string.calibration_wizard_live_yaw,
                telemetry.pitchDeg,
                telemetry.rollDeg,
                telemetry.speedMps * 3.6f,
            ),
            color = GaugeGreen,
            modifier = Modifier.testTag("calibration_wizard_test_drive_readout"),
        )
    }
}

@Composable
private fun RowNav(
    stepIndex: Int,
    lastIndex: Int,
    onBackStep: () -> Unit,
    onNextStep: () -> Unit,
    onFinish: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        if (stepIndex > 0) {
            Button(onClick = onBackStep, modifier = Modifier.testTag("calibration_wizard_prev")) {
                Text(stringResource(R.string.calibration_wizard_back))
            }
        }
        Button(onClick = onNextStep, modifier = Modifier.testTag("calibration_wizard_next")) {
            Text(
                if (onFinish) {
                    stringResource(R.string.calibration_wizard_finish)
                } else {
                    stringResource(R.string.onboarding_next)
                },
            )
        }
    }
}
