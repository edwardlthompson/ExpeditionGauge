package dev.foss.expeditiongauge.ui.hudtile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.hudtile.HudTileId
import dev.foss.expeditiongauge.hudtile.HudTileLayout

@Composable
fun HudTileLayoutDialog(
    order: List<HudTileId>,
    onCycle: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hud_tile_title)) },
        text = {
            Text(
                text = HudTileLayout.summary(order),
                modifier = Modifier.testTag("hud_tile_order"),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onCycle,
                modifier = Modifier.testTag("hud_tile_cycle"),
            ) { Text(stringResource(R.string.hud_tile_cycle)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("hud_tile_close"),
            ) { Text(stringResource(R.string.hud_tile_close)) }
        },
        modifier = Modifier.testTag("hud_tile_dialog"),
    )
}
