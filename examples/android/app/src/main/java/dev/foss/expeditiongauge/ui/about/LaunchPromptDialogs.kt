package dev.foss.expeditiongauge.ui.about

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R

@Composable
fun DonateNudgeDialog(
    onDonate: () -> Unit,
    onNotNow: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onNotNow,
        title = { Text(stringResource(R.string.about_donate_nudge_title)) },
        text = { Text(stringResource(R.string.about_donate_nudge_message)) },
        confirmButton = {
            TextButton(onClick = onDonate, modifier = Modifier.testTag("donate_nudge_venmo")) {
                Text(stringResource(R.string.about_donate))
            }
        },
        dismissButton = {
            TextButton(onClick = onNotNow, modifier = Modifier.testTag("donate_nudge_later")) {
                Text(stringResource(R.string.about_not_now))
            }
        },
        modifier = Modifier.testTag("donate_nudge_dialog"),
    )
}

@Composable
fun UpdateAvailableDialog(
    version: String,
    onInstall: () -> Unit,
    onLater: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onLater,
        title = { Text(stringResource(R.string.about_update_title)) },
        text = { Text(stringResource(R.string.about_update_message, version)) },
        confirmButton = {
            TextButton(onClick = onInstall, modifier = Modifier.testTag("update_prompt_install")) {
                Text(stringResource(R.string.about_install))
            }
        },
        dismissButton = {
            TextButton(onClick = onLater, modifier = Modifier.testTag("update_prompt_later")) {
                Text(stringResource(R.string.about_later))
            }
        },
        modifier = Modifier.testTag("update_prompt_dialog"),
    )
}
