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
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ble.tpms.TpmsIdCandidate
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun TpmsWizardResolveStep(
    corner: ImuPlacement,
    sensorId: String,
    candidates: List<TpmsIdCandidate>,
    onSelectMac: (String) -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("tpms_wizard_resolve"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.tpms_wizard_resolve_title, corner.label),
            style = MaterialTheme.typography.titleLarge,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.tpms_wizard_resolve_id, sensorId),
            color = GaugeScaleWhite,
        )
        Text(
            text = stringResource(R.string.tpms_wizard_resolve_hint),
            color = GaugeScaleWhite,
        )
        if (candidates.isEmpty()) {
            Text(
                text = stringResource(R.string.tpms_wizard_resolve_waiting),
                color = GaugeScaleWhite,
                modifier = Modifier.testTag("tpms_wizard_resolve_waiting"),
            )
        } else {
            candidates.forEach { candidate ->
                val label = stringResource(R.string.tpms_wizard_resolve_live, candidate.macAddress)
                Button(
                    onClick = { onSelectMac(candidate.macAddress) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tpms_wizard_resolve_pick"),
                ) {
                    Text(label)
                }
            }
        }
        OutlinedButton(onClick = onRescan, modifier = Modifier.testTag("tpms_wizard_resolve_rescan")) {
            Text(stringResource(R.string.tpms_wizard_rescan))
        }
    }
}
