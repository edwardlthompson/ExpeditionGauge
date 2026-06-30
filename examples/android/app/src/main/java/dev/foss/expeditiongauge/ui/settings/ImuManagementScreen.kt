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
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ble.BleImuManager
import dev.foss.expeditiongauge.ble.ImuDeviceSession
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun ImuManagementScreen(
    sessions: List<ImuDeviceSession>,
    bleImuManager: BleImuManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.imu_management_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = { bleImuManager.startScan() }) {
            Text(stringResource(R.string.imu_scan))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
            items(sessions, key = { it.deviceId }) { session ->
                ImuDeviceRow(session, bleImuManager)
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImuDeviceRow(session: ImuDeviceSession, manager: BleImuManager) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = session.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.imu_signal_quality, session.signalQuality.name),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
                if (session.connected) {
                    Button(onClick = { manager.disconnect(session.deviceId) }) {
                        Text(stringResource(R.string.imu_disconnect))
                    }
                } else {
                    Button(onClick = { manager.connect(session.deviceId) }) {
                        Text(stringResource(R.string.imu_connect))
                    }
                }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ImuPlacement.entries.filter { it != ImuPlacement.Unassigned }.forEach { placement ->
                FilterChip(
                    selected = session.placement == placement,
                    onClick = { manager.setPlacement(session.deviceId, placement) },
                    label = { Text(placement.label) },
                )
            }
        }
    }
}
