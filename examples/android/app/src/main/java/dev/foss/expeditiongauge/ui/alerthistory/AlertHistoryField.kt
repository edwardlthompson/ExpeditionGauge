package dev.foss.expeditiongauge.ui.alerthistory

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.AlertHistoryStore
import kotlinx.coroutines.launch

@Composable
fun AlertHistoryField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { AlertHistoryStore(context) }
    val entries by store.entries.collectAsStateWithLifecycle(emptyList())
    Text(
        text = stringResource(R.string.alerts_history_title, entries.size),
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier.testTag("alerts_history_title"),
    )
    entries.take(8).forEach { entry ->
        Text(
            text = stringResource(R.string.alerts_history_row, entry.type, entry.value, entry.threshold),
            style = MaterialTheme.typography.labelSmall,
        )
    }
    if (entries.isNotEmpty()) {
        Button(
            onClick = { scope.launch { store.clear() } },
            modifier = Modifier.fillMaxWidth().testTag("alerts_history_clear"),
        ) {
            Text(stringResource(R.string.alerts_history_clear))
        }
    }
}
