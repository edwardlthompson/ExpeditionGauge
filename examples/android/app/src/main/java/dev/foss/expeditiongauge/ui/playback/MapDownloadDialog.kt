package dev.foss.expeditiongauge.ui.playback

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R

@Composable
fun MapDownloadDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDownloadWifi: () -> Unit,
    onDownloadCellular: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.map_download_title)) },
        text = { Text(stringResource(R.string.map_download_message)) },
        confirmButton = {
            TextButton(onClick = onDownloadWifi) {
                Text(stringResource(R.string.map_download_wifi))
            }
        },
        dismissButton = {
            TextButton(onClick = onDownloadCellular) {
                Text(stringResource(R.string.map_download_cellular))
            }
        },
    )
}
