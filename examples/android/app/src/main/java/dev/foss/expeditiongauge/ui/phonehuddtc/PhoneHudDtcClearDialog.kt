package dev.foss.expeditiongauge.ui.phonehuddtc

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R

@Composable
fun PhoneHudDtcClearDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dtc_clear_title)) },
        text = { Text(stringResource(R.string.dtc_clear_body)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("dtc_clear_confirm"),
            ) { Text(stringResource(R.string.dtc_clear_confirm)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dtc_clear_cancel"),
            ) { Text(stringResource(R.string.dtc_clear_cancel)) }
        },
        modifier = Modifier.testTag("dtc_clear_dialog"),
    )
}
