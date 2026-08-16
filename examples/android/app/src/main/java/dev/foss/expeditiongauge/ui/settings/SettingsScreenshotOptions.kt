package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.HudScreenshotMode
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreenshotOptions(
    screenshotMode: HudScreenshotMode,
    onScreenshotModeSelected: (HudScreenshotMode) -> Unit,
) {
    Text(text = stringResource(R.string.dashboard_screenshot_mode))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        FilterChip(
            selected = screenshotMode == HudScreenshotMode.FULL_SCREEN,
            onClick = { onScreenshotModeSelected(HudScreenshotMode.FULL_SCREEN) },
            label = { Text(stringResource(R.string.dashboard_screenshot_full)) },
            modifier = Modifier.testTag("settings_screenshot_full"),
        )
        FilterChip(
            selected = screenshotMode == HudScreenshotMode.EACH_CUBE,
            onClick = { onScreenshotModeSelected(HudScreenshotMode.EACH_CUBE) },
            label = { Text(stringResource(R.string.dashboard_screenshot_cubes)) },
            modifier = Modifier.testTag("settings_screenshot_cubes"),
        )
    }
}
