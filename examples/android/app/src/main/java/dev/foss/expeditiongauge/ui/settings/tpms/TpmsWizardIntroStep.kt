package dev.foss.expeditiongauge.ui.settings.tpms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun TpmsWizardIntroStep(
    bluetoothEnabled: Boolean,
    cameraGranted: Boolean,
    onEnableBluetooth: () -> Unit,
    onAllowCamera: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onContinueManual: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("tpms_wizard_intro"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(text = stringResource(R.string.tpms_wizard_intro), color = GaugeScaleWhite)
        Text(text = stringResource(R.string.tpms_wizard_camera_rationale), color = GaugeScaleWhite)
        if (!bluetoothEnabled) {
            Text(
                text = stringResource(R.string.tpms_wizard_intro_bt_off),
                color = MaterialTheme.colorScheme.error,
            )
            Button(onClick = onEnableBluetooth) {
                Text(stringResource(R.string.tpms_wizard_enable_bt))
            }
        }
        if (!cameraGranted) {
            Button(
                onClick = onAllowCamera,
                modifier = Modifier.testTag("tpms_wizard_allow_camera"),
            ) {
                Text(stringResource(R.string.tpms_wizard_allow_camera))
            }
            OutlinedButton(onClick = onOpenAppSettings) {
                Text(stringResource(R.string.tpms_wizard_open_app_settings))
            }
            OutlinedButton(
                onClick = onContinueManual,
                modifier = Modifier.testTag("tpms_wizard_continue_manual"),
            ) {
                Text(stringResource(R.string.tpms_wizard_continue_manual))
            }
        }
        Button(
            onClick = onContinue,
            modifier = Modifier.testTag("tpms_wizard_continue"),
        ) {
            Text(stringResource(R.string.tpms_wizard_continue))
        }
    }
}
