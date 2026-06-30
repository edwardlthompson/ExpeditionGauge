package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun SessionListScreen(
    database: ExpeditionGaugeDatabase,
    onSessionSelected: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessions by database.recordingSessionDao().observeAll().collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.playback_sessions),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            items(sessions, key = { it.id }) { session ->
                SessionCard(session = session, onClick = { onSessionSelected(session.id) })
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@Composable
private fun SessionCard(session: RecordingSessionEntity, onClick: () -> Unit) {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(session.startTimeMs))
    val endMs = session.endTimeMs ?: session.startTimeMs
    val durationMin = TimeUnit.MILLISECONDS.toMinutes(endMs - session.startTimeMs).coerceAtLeast(0)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = SpacingMd),
    ) {
        Column(modifier = Modifier.padding(SpacingMd)) {
            Text(text = session.name, color = GaugeYellow, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.session_duration_max_speed, durationMin, 0f),
                color = GaugeScaleWhite,
            )
            Text(text = date, color = GaugeScaleWhite, style = MaterialTheme.typography.bodySmall)
        }
    }
}
