package dev.foss.expeditiongauge.map

import android.content.Context
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase

object SessionMapPrefetch {
    suspend fun enqueueAfterRecording(
        context: Context,
        database: ExpeditionGaugeDatabase,
        sessionId: Long,
    ) {
        val samples = database.sampleDao().getBySession(sessionId)
        val bounds = MapRegionBounds.fromSamples(samples) ?: return
        MapTilePrefetchWorker.enqueueSessionPrefetch(context, sessionId, bounds)
    }
}
