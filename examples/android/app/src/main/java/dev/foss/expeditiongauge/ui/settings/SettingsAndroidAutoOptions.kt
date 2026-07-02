package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun SettingsAndroidAutoOptions(
    modifier: Modifier = Modifier,
) {
    if (!FeatureFlags.androidAutoCapable) return
    Text(
        text = stringResource(R.string.settings_android_auto_heading),
        modifier = modifier.fillMaxWidth(),
    )
    Text(
        text = stringResource(R.string.settings_android_auto_auto_connect),
        color = GaugeScaleWhite,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_android_auto_info"),
    )
    Text(
        text = stringResource(R.string.settings_android_auto_setup_hint),
        color = GaugeScaleWhite,
        modifier = Modifier.fillMaxWidth(),
    )
}
