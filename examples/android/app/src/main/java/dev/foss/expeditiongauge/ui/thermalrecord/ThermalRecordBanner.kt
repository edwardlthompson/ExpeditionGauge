package dev.foss.expeditiongauge.ui.thermalrecord

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.thermal.ThermalStatus
import dev.foss.expeditiongauge.thermalrecord.ThermalRecordThrottle
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.launch

@Composable
fun ThermalRecordBanner(
    status: ThermalStatus,
    modifier: Modifier = Modifier,
) {
    val interval = ThermalRecordThrottle.suggestedIntervalMs(status) ?: return
    val context = LocalContext.current
    val prefs = remember { SettingsPreferences(context) }
    val scope = rememberCoroutineScope()
    val hz = ThermalRecordThrottle.hzLabel(interval)
    Text(
        text = stringResource(R.string.thermal_warning),
        color = GaugeYellow,
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingMd)
            .testTag("thermal_record_banner"),
    )
    TextButton(
        onClick = { scope.launch { prefs.setLogIntervalMs(interval) } },
        modifier = Modifier.testTag("thermal_record_apply"),
    ) {
        Text(text = stringResource(R.string.thermal_record_apply_hz, hz), color = GaugeYellow)
    }
}
