package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R

@Composable
fun AutocalConfirmDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.autocal_confirm_title)) },
        text = { Text(stringResource(R.string.autocal_confirm_body)) },
        confirmButton = {
            TextButton(
                onClick = onAccept,
                modifier = Modifier.testTag("autocal_confirm"),
            ) {
                Text(stringResource(R.string.autocal_confirm_accept))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("autocal_dismiss"),
            ) {
                Text(stringResource(R.string.autocal_confirm_dismiss))
            }
        },
        modifier = Modifier.testTag("autocal_dialog"),
    )
}
