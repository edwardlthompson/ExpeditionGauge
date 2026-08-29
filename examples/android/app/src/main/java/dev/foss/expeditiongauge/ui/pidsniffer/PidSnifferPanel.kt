package dev.foss.expeditiongauge.ui.pidsniffer

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
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun PidSnifferPanel(
    last: String?,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by remember { mutableStateOf("010C") }
    OutlinedTextField(
        value = draft,
        onValueChange = { draft = it },
        label = { Text(stringResource(R.string.pid_sniffer_command)) },
        modifier = modifier.fillMaxWidth().testTag("pid_sniffer_command"),
        singleLine = true,
    )
    Button(
        onClick = { onSend(draft) },
        modifier = Modifier.fillMaxWidth().testTag("pid_sniffer_send"),
    ) {
        Text(stringResource(R.string.pid_sniffer_send))
    }
    last?.let {
        Text(
            text = stringResource(R.string.pid_sniffer_last, it),
            color = GaugeScaleWhite,
            modifier = Modifier.testTag("pid_sniffer_last"),
        )
    }
}
