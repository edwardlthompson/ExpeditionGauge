package dev.foss.expeditiongauge.ui.piddiscovery

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.piddiscovery.PidDiscovery

@Composable
fun PidDiscoveryDialog(
    pids: Set<Int>?,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    val body = pids?.let { PidDiscovery.summary(it) }
        ?: stringResource(R.string.pid_discovery_empty)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pid_discovery_title)) },
        text = {
            Text(text = body, modifier = Modifier.testTag("pid_discovery_body"))
        },
        confirmButton = {
            TextButton(
                onClick = onApply,
                enabled = !pids.isNullOrEmpty(),
                modifier = Modifier.testTag("pid_discovery_apply"),
            ) { Text(stringResource(R.string.pid_discovery_apply)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("pid_discovery_close"),
            ) { Text(stringResource(R.string.pid_discovery_close)) }
        },
        modifier = Modifier.testTag("pid_discovery_dialog"),
    )
}
