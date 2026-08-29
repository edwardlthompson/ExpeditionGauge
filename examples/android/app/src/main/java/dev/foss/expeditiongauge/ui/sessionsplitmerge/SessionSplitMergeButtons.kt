package dev.foss.expeditiongauge.ui.sessionsplitmerge

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.media.SessionDeleteService
import dev.foss.expeditiongauge.sessionsplitmerge.SessionSplitMerge
import dev.foss.expeditiongauge.sessionsplitmerge.SessionSplitMergeRepo
import kotlinx.coroutines.launch

@Composable
fun SessionSplitMergeButtons(
    sessionId: Long,
    database: ExpeditionGaugeDatabase,
    deleteService: SessionDeleteService,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val repo = remember {
        SessionSplitMergeRepo(
            sessionDao = database.recordingSessionDao(),
            sampleDao = database.sampleDao(),
            deleteSession = { id -> deleteService.deleteSession(id) },
        )
    }
    Button(
        onClick = {
            scope.launch {
                val samples = database.sampleDao().getBySession(sessionId)
                val at = SessionSplitMerge.midpointMs(samples) ?: return@launch
                repo.splitAt(sessionId, at)
                onDone()
            }
        },
        modifier = modifier.testTag("session_split_midpoint"),
    ) {
        Text(stringResource(R.string.session_split_midpoint))
    }
}
