package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.crash.CrashLogStore
import dev.foss.expeditiongauge.crash.CrashShareLauncher
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun SettingsAndroidAutoOptions(
    modifier: Modifier = Modifier,
) {
    if (!FeatureFlags.androidAutoCapable) return
    val context = LocalContext.current
    var preview by remember {
        mutableStateOf(CrashLogStore.fromContext(context).previewLines())
    }
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
    Text(
        text = stringResource(R.string.settings_crash_heading),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    )
    Text(
        text = preview ?: stringResource(R.string.settings_crash_none),
        color = GaugeScaleWhite,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_crash_preview"),
    )
    Button(
        onClick = { CrashShareLauncher.shareLastCrash(context) },
        enabled = preview != null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_crash_share"),
    ) {
        Text(text = stringResource(R.string.settings_crash_share))
    }
    Button(
        onClick = {
            CrashLogStore.fromContext(context).clear()
            preview = null
        },
        enabled = preview != null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_crash_clear"),
    ) {
        Text(text = stringResource(R.string.settings_crash_clear))
    }
}
