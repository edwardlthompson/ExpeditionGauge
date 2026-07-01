package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.stats.RichSessionCard
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SessionListScreen(
    database: ExpeditionGaugeDatabase,
    statsSummaries: List<SessionStatsSummary> = emptyList(),
    onSessionSelected: (Long) -> Unit,
    onSessionCompare: ((Long, Long) -> Unit)? = null,
    onSessionEdit: (Long) -> Unit,
    onSessionExportZip: ((Long) -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var activityFilter by remember { mutableStateOf<ActivityType?>(null) }
    val sessionsFlow = remember(searchQuery, activityFilter) {
        if (FeatureFlags.activityLibraryEnabled) {
            database.recordingSessionDao().observeFiltered(
                activityType = activityFilter?.name.orEmpty(),
                query = searchQuery.trim(),
            )
        } else if (searchQuery.isBlank()) {
            database.recordingSessionDao().observeAll()
        } else {
            database.recordingSessionDao().observeSearch(searchQuery)
        }
    }
    val sessions by sessionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val summaryById = remember(statsSummaries) { statsSummaries.associateBy { it.sessionId } }

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
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.session_search_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SpacingMd)
                .testTag("session_search"),
            singleLine = true,
        )
        if (FeatureFlags.activityLibraryEnabled) {
            ActivityTypeFilterRow(
                selected = activityFilter,
                onSelected = { activityFilter = it },
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            items(sessions, key = { it.id }) { session ->
                val summary = summaryById[session.id]
                if (summary != null) {
                    val compareTarget = sessions.firstOrNull { it.id != session.id }?.id
                    RichSessionCard(
                        summary = summary,
                        onPlay = { onSessionSelected(session.id) },
                        onCompare = if (compareTarget != null && onSessionCompare != null) {
                            { onSessionCompare(session.id, compareTarget) }
                        } else {
                            null
                        },
                        onExport = onSessionExportZip?.let { export ->
                            { export(session.id) }
                        },
                        onEdit = { onSessionEdit(session.id) },
                    )
                } else {
                    FallbackSessionCard(
                        session = session,
                        onPlay = { onSessionSelected(session.id) },
                        onEdit = { onSessionEdit(session.id) },
                    )
                }
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@Composable
private fun FallbackSessionCard(
    session: RecordingSessionEntity,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
) {
    RichSessionCard(
        summary = SessionStatsSummary(
            sessionId = session.id,
            name = session.name,
            durationMs = ((session.endTimeMs ?: session.startTimeMs) - session.startTimeMs).coerceAtLeast(0),
            maxBetaDeg = null,
            peakLatG = null,
            slipEventCount = 0,
            eventCount = 0,
            activityType = session.activityType,
        ),
        onPlay = onPlay,
    )
    Button(
        onClick = onEdit,
        modifier = Modifier
            .testTag("session_edit")
            .semantics { contentDescription = "Edit metadata" },
    ) {
        Text(stringResource(R.string.session_metadata_edit))
    }
}
