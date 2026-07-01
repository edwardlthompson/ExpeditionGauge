package dev.foss.expeditiongauge.ui.settings

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsRecordingOptions(
    autoRecordEnabled: Boolean,
    selectedAddresses: Set<String>,
    onAutoRecordEnabledChange: (Boolean) -> Unit,
    onDeviceToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bondedDevices = remember {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            emptyList()
        } else {
            BluetoothAdapter.getDefaultAdapter()?.bondedDevices?.toList().orEmpty()
        }
    }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(text = stringResource(R.string.settings_auto_record_heading))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.settings_auto_record_toggle))
            Switch(
                checked = autoRecordEnabled,
                onCheckedChange = onAutoRecordEnabledChange,
                modifier = Modifier.testTag("settings_auto_record_enabled"),
            )
        }
        Text(text = stringResource(R.string.settings_auto_record_hint))
        bondedDevices.forEach { device ->
            BondedDeviceRow(
                device = device,
                checked = device.address in selectedAddresses,
                onToggle = { onDeviceToggle(device.address, it) },
            )
        }
        if (bondedDevices.isEmpty()) {
            Text(text = stringResource(R.string.settings_auto_record_no_devices))
        }
    }
}

@Composable
private fun BondedDeviceRow(
    device: BluetoothDevice,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val name = device.name ?: device.address
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("auto_record_device_${device.address}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onToggle)
        Text(text = "$name (${device.address})")
    }
}
