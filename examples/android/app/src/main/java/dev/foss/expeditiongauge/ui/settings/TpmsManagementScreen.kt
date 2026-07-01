package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ble.tpms.BleTpmsManager
import dev.foss.expeditiongauge.ble.tpms.TpmsDeviceSession
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeMenuSurface
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun TpmsManagementScreen(
    sessions: List<TpmsDeviceSession>,
    bleTpmsManager: BleTpmsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GaugeMenuSurface(modifier = modifier) {
        GaugeBackHandler(onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
        Text(
            text = stringResource(R.string.tpms_management_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Button(
            onClick = { bleTpmsManager.startScan() },
            modifier = Modifier.testTag("tpms_scan"),
        ) {
            Text(stringResource(R.string.tpms_scan))
        }
        Button(onClick = { bleTpmsManager.stopScan() }) {
            Text(stringResource(R.string.tpms_stop_scan))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
            items(sessions, key = { it.macAddress }) { session ->
                TpmsDeviceRow(session, bleTpmsManager)
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TpmsDeviceRow(session: TpmsDeviceSession, manager: BleTpmsManager) {
    val reading = session.lastReading
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = session.macAddress, style = MaterialTheme.typography.titleMedium)
                reading?.let {
                    Text(
                        text = stringResource(
                            R.string.tpms_reading_line,
                            it.pressureKpa,
                            it.tempC,
                            session.parserId,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Text(
                text = if (session.stale) {
                    stringResource(R.string.tpms_stale)
                } else {
                    stringResource(R.string.tpms_active)
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ImuPlacement.entries.filter { it != ImuPlacement.Unassigned }.forEach { placement ->
                FilterChip(
                    selected = session.corner == placement,
                    onClick = { manager.assignCorner(session.macAddress, placement) },
                    label = { Text(placement.label) },
                )
            }
        }
    }
}
