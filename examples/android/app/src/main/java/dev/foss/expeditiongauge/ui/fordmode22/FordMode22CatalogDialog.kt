package dev.foss.expeditiongauge.ui.fordmode22

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.fordmode22.FordMode22Catalog

@Composable
fun FordMode22CatalogDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ford_mode22_title)) },
        text = {
            Text(
                text = FordMode22Catalog.summary(),
                modifier = Modifier.testTag("ford_mode22_catalog"),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ford_mode22_close"),
            ) { Text(stringResource(R.string.ford_mode22_close)) }
        },
        modifier = Modifier.testTag("ford_mode22_dialog"),
    )
}
