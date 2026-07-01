package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.car.CarTelemetryHost
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsAndroidAutoOptions(
    androidAutoEnabled: Boolean,
    onAndroidAutoEnabledChange: (Boolean) -> Unit,
    allowedMetrics: Set<String>,
    onToggleMetric: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!FeatureFlags.androidAutoCapable) return
    Text(
        text = stringResource(R.string.settings_android_auto_heading),
        modifier = modifier.fillMaxWidth(),
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_android_auto_enable),
        checked = androidAutoEnabled,
        onCheckedChange = onAndroidAutoEnabledChange,
        modifier = Modifier.testTag("settings_android_auto_enable"),
    )
    if (androidAutoEnabled) {
        Text(text = stringResource(R.string.settings_android_auto_metrics))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            CarTelemetryHost.defaultPriority.forEach { key ->
                FilterChip(
                    selected = key in allowedMetrics,
                    onClick = { onToggleMetric(key) },
                    label = { Text(key) },
                    modifier = Modifier.testTag("settings_android_auto_metric_$key"),
                )
            }
        }
    }
}
