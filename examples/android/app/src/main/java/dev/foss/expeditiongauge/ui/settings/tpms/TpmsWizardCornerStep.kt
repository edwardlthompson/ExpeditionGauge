package dev.foss.expeditiongauge.ui.settings.tpms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ble.tpms.TpmsQrParseResult
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun TpmsWizardCornerStep(
    corner: ImuPlacement,
    stepIndex: Int,
    stepCount: Int,
    showCamera: Boolean,
    parseError: TpmsQrParseResult.Reason?,
    showManual: Boolean,
    onShowManual: () -> Unit,
    onRawPayload: (String) -> Unit,
    onManualSubmit: (String) -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var manualText by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxWidth().testTag("tpms_wizard_corner"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.tpms_wizard_step, stepIndex + 1, stepCount),
            color = GaugeScaleWhite,
        )
        Text(
            text = stringResource(R.string.tpms_wizard_corner_title, corner.label),
            style = MaterialTheme.typography.titleLarge,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.tpms_wizard_corner_hint, corner.label),
            color = GaugeScaleWhite,
        )
        if (showCamera) {
            TpmsQrScanner(
                enabled = true,
                onRawPayload = onRawPayload,
                modifier = Modifier.testTag("tpms_wizard_scanner"),
            )
        }
        parseError?.let { reason ->
            Text(
                text = stringResource(errorStringRes(reason)),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("tpms_wizard_parse_error"),
            )
        }
        if (showManual) {
            OutlinedTextField(
                value = manualText,
                onValueChange = { manualText = it },
                label = { Text(stringResource(R.string.tpms_wizard_manual_title)) },
                placeholder = { Text(stringResource(R.string.tpms_wizard_manual_hint)) },
                modifier = Modifier.fillMaxWidth().testTag("tpms_wizard_manual_field"),
                singleLine = true,
            )
            Button(
                onClick = { onManualSubmit(manualText) },
                modifier = Modifier.testTag("tpms_wizard_manual_submit"),
            ) {
                Text(stringResource(R.string.tpms_wizard_manual_confirm))
            }
        } else {
            OutlinedButton(onClick = onShowManual, modifier = Modifier.testTag("tpms_wizard_manual")) {
                Text(stringResource(R.string.tpms_wizard_enter_manual))
            }
        }
        OutlinedButton(onClick = onSkip, modifier = Modifier.testTag("tpms_wizard_skip")) {
            Text(stringResource(R.string.tpms_wizard_skip))
        }
        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.tpms_wizard_back))
        }
    }
}

@Composable
fun TpmsWizardConfirmStep(
    corner: ImuPlacement,
    mac: String,
    acceptArmed: Boolean,
    waitingHint: Boolean,
    onAccept: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("tpms_wizard_confirm"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.tpms_wizard_confirm_title),
            style = MaterialTheme.typography.titleLarge,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.tpms_wizard_confirm_body, mac, corner.label),
            color = GaugeScaleWhite,
        )
        if (waitingHint) {
            Text(
                text = stringResource(R.string.tpms_wizard_saved_waiting),
                color = GaugeScaleWhite,
            )
        }
        Button(
            onClick = onAccept,
            enabled = acceptArmed,
            modifier = Modifier.testTag("tpms_wizard_accept"),
        ) {
            Text(stringResource(R.string.tpms_wizard_accept))
        }
        OutlinedButton(onClick = onRescan, modifier = Modifier.testTag("tpms_wizard_rescan")) {
            Text(stringResource(R.string.tpms_wizard_rescan))
        }
    }
}

internal fun errorStringRes(reason: TpmsQrParseResult.Reason): Int = when (reason) {
    TpmsQrParseResult.Reason.Empty -> R.string.tpms_wizard_error_empty
    TpmsQrParseResult.Reason.NoMac -> R.string.tpms_wizard_error_no_mac
    TpmsQrParseResult.Reason.BadLength -> R.string.tpms_wizard_error_bad_length
    TpmsQrParseResult.Reason.BadCharset -> R.string.tpms_wizard_error_bad_charset
}
