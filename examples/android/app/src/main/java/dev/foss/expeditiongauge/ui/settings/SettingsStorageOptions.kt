package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.recording.SessionStorageBudget
import dev.foss.expeditiongauge.ui.storagemeter.StorageMeterBar
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsStorageOptions(
    storagePercent: Int,
    usedBytes: Long,
    allowedBytes: Long,
    onPercentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(text = stringResource(R.string.settings_storage_heading))
        Text(
            text = stringResource(
                R.string.settings_storage_summary,
                formatStorageMb(usedBytes),
                formatStorageMb(allowedBytes),
                storagePercent,
            ),
            modifier = Modifier.testTag("settings_storage_summary"),
        )
        StorageMeterBar(usedBytes = usedBytes, allowedBytes = allowedBytes)
        Text(text = stringResource(R.string.settings_storage_loop_hint))
        Slider(
            value = storagePercent.toFloat(),
            onValueChange = { onPercentChange(it.toInt()) },
            valueRange = SessionStorageBudget.MIN_PERCENT.toFloat()..SessionStorageBudget.MAX_PERCENT.toFloat(),
            steps = 16,
            modifier = Modifier.testTag("settings_storage_percent"),
        )
        Text(text = stringResource(R.string.settings_storage_percent_label, storagePercent))
    }
}

private fun formatStorageMb(bytes: Long): String = "%.1f MB".format(bytes / (1024.0 * 1024.0))
