package dev.foss.expeditiongauge.ui.wifielm

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.wifielm.WifiElm327

@Composable
fun WifiElm327Field(
    selectedEndpoint: String?,
    onConnect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by remember(selectedEndpoint) {
        mutableStateOf(WifiElm327.display(selectedEndpoint))
    }
    OutlinedTextField(
        value = draft,
        onValueChange = { draft = it },
        label = { Text(stringResource(R.string.wifi_elm327_host)) },
        modifier = modifier.fillMaxWidth().testTag("wifi_elm327_host"),
        singleLine = true,
    )
    Button(
        onClick = {
            WifiElm327.parseDisplay(draft)?.let { onConnect(WifiElm327.encode(it.host, it.port)) }
        },
        modifier = Modifier.fillMaxWidth().testTag("wifi_elm327_connect"),
    ) {
        Text(stringResource(R.string.wifi_elm327_connect))
    }
}
