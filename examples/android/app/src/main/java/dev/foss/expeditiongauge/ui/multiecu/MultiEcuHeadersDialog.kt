package dev.foss.expeditiongauge.ui.multiecu

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.multiecu.MultiEcuHeaders

@Composable
fun MultiEcuHeadersDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.multi_ecu_title)) },
        text = {
            Text(
                text = MultiEcuHeaders.summary(),
                modifier = Modifier.testTag("multi_ecu_headers"),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("multi_ecu_close"),
            ) { Text(stringResource(R.string.multi_ecu_close)) }
        },
        modifier = Modifier.testTag("multi_ecu_dialog"),
    )
}
