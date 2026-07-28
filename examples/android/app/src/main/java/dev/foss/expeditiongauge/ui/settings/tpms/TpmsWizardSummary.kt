package dev.foss.expeditiongauge.ui.settings.tpms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun TpmsWizardSummary(
    assigned: Map<ImuPlacement, String>,
    skipped: Set<ImuPlacement>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("tpms_wizard_summary"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.tpms_wizard_summary_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        TpmsPairingWizardState.CORNERS.forEach { corner ->
            val value = assigned[corner]
                ?: if (corner in skipped) {
                    stringResource(R.string.tpms_wizard_summary_skipped)
                } else {
                    stringResource(R.string.tpms_wizard_summary_skipped)
                }
            Text(
                text = stringResource(R.string.tpms_wizard_summary_row, corner.label, value),
                color = GaugeScaleWhite,
            )
        }
        Button(onClick = onDone, modifier = Modifier.testTag("tpms_wizard_done")) {
            Text(stringResource(R.string.tpms_wizard_done))
        }
    }
}
