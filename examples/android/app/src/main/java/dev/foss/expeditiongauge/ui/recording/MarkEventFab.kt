package dev.foss.expeditiongauge.ui.recording

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R

@Composable
fun MarkEventFab(
    visible: Boolean,
    onMarkEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    FloatingActionButton(
        onClick = onMarkEvent,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = Icons.Filled.Flag,
            contentDescription = stringResource(R.string.mark_event_fab),
        )
    }
}
