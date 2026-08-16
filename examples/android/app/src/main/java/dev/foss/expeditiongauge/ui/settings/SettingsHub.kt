package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsHub(
    onCategory: (SettingsCategory) -> Unit,
    onAbout: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_hub"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_display),
            hint = stringResource(R.string.settings_hub_display_hint),
            icon = Icons.Filled.Tune,
            testTag = "settings_hub_display",
            onClick = { onCategory(SettingsCategory.Display) },
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_recording),
            hint = stringResource(R.string.settings_hub_recording_hint),
            icon = Icons.Filled.Videocam,
            testTag = "settings_hub_recording",
            onClick = { onCategory(SettingsCategory.Recording) },
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_alerts),
            hint = stringResource(R.string.settings_hub_alerts_hint),
            icon = Icons.Filled.Notifications,
            testTag = "settings_hub_alerts",
            onClick = { onCategory(SettingsCategory.Alerts) },
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_hardware),
            hint = stringResource(R.string.settings_hub_hardware_hint),
            icon = Icons.Filled.Sensors,
            testTag = "settings_hub_hardware",
            onClick = { onCategory(SettingsCategory.Hardware) },
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_maps),
            hint = stringResource(R.string.settings_hub_maps_hint),
            icon = Icons.Filled.Map,
            testTag = "settings_hub_maps",
            onClick = { onCategory(SettingsCategory.Maps) },
        )
        SettingsHubRow(
            label = stringResource(R.string.settings_hub_advanced),
            hint = stringResource(R.string.settings_hub_advanced_hint),
            icon = Icons.Filled.Build,
            testTag = "settings_hub_advanced",
            onClick = { onCategory(SettingsCategory.Advanced) },
        )
        SettingsHubRow(
            label = stringResource(R.string.about_open),
            hint = stringResource(R.string.settings_hub_about_hint),
            icon = Icons.Filled.Info,
            testTag = "settings_hub_about",
            onClick = onAbout,
        )
        Button(onClick = onClose, modifier = Modifier.testTag("settings_close")) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@Composable
private fun SettingsHubRow(
    label: String,
    hint: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(vertical = SpacingMd / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = GaugeYellow)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = GaugeScaleWhite)
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = GaugeScaleWhite,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.dashboard_menu_submenu),
            tint = GaugeScaleWhite,
        )
    }
}
