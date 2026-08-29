package dev.foss.expeditiongauge.ui.settingsjsonbackup

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settingsjsonbackup.SettingsBackupStore
import dev.foss.expeditiongauge.settingsjsonbackup.SettingsJsonBackup
import kotlinx.coroutines.launch

@Composable
fun SettingsBackupButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { SettingsBackupStore(context) }
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = {
            val blob = SettingsJsonBackup.encode(
                mapOf(
                    "speed_unit" to "metric",
                    "log_interval_ms" to "50",
                    "live_telemetry" to "false",
                ),
            )
            scope.launch { store.save(blob) }
        },
        modifier = modifier.testTag("settings_json_backup"),
    ) {
        Text(stringResource(R.string.settings_json_backup))
    }
}
