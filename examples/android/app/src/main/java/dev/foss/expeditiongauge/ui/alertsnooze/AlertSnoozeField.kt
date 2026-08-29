package dev.foss.expeditiongauge.ui.alertsnooze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
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
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.alertsnooze.AlertSnooze
import dev.foss.expeditiongauge.settings.AlertSnoozeStore
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertSnoozeField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { AlertSnoozeStore(context) }
    val untilByType by store.untilByType.collectAsStateWithLifecycle(emptyMap())
    val nowMs = System.currentTimeMillis()
    Text(text = stringResource(R.string.alerts_snooze_label))
    FlowRow(
        modifier = modifier.testTag("alerts_snooze_row"),
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        AlertType.entries.forEach { type ->
            val snoozed = AlertSnooze.suppressed(untilByType[type], nowMs)
            FilterChip(
                selected = snoozed,
                onClick = {
                    scope.launch {
                        if (snoozed) store.clear(type) else store.snooze(type, System.currentTimeMillis())
                    }
                },
                label = { Text(type.name.lowercase().replace('_', ' ')) },
                modifier = Modifier.testTag("alerts_snooze_${type.name.lowercase()}"),
            )
        }
    }
}
