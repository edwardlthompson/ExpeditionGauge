package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.car.aaparkedlibrary.AaParkedLibrary
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Collects recorded sessions into parked-library HUD rows. */
internal class AndroidAutoBridgeSessions(
    private val scope: CoroutineScope,
    private val sessions: Flow<List<RecordingSessionEntity>>,
    private val onRows: (List<DriveHudRow>) -> Unit,
    private val invalidateForce: () -> Unit,
) {
    fun startCollect() {
        scope.launch {
            sessions.collect { list ->
                onRows(
                    AaParkedLibrary.rows(
                        list.map { Triple(it.name, it.startTimeMs, it.endTimeMs) },
                    ),
                )
                invalidateForce()
            }
        }
    }
}
