package dev.foss.expeditiongauge.ui.settingsqrtransfer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settingsqrtransfer.SettingsQrTransfer

@Composable
fun SettingsQrLabel(modifier: Modifier = Modifier) {
    val payload = SettingsQrTransfer.encodePairs(mapOf("speed_unit" to "metric"))
    Text(
        text = stringResource(R.string.settings_qr_transfer) + " " + payload,
        modifier = modifier.testTag("settings_qr_transfer"),
    )
}
