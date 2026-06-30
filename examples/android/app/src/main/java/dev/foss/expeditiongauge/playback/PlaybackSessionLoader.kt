package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.ghost.GhostLapOverlay

object PlaybackSessionLoader {
    private suspend fun loadAlertTimestamps(services: ExpeditionGaugeServices, sessionId: Long): List<Long> =
        services.database.alertEventDao().getBySession(sessionId).map { it.timestampMs }

    private suspend fun loadMarkEventTimestamps(services: ExpeditionGaugeServices, sessionId: Long): List<Long> =
        services.database.sessionEventDao().getBySession(sessionId).map { it.timestampMs }

    suspend fun load(services: ExpeditionGaugeServices, sessionId: Long) {
        val samples = services.database.sampleDao().getBySession(sessionId)
        val alertTimestamps = loadAlertTimestamps(services, sessionId)
        val markTimestamps = loadMarkEventTimestamps(services, sessionId)
        val markers = PlaybackEngine.computeMarkers(samples, alertTimestamps, markTimestamps)
        val config = services.lapTimingService.getTrackConfig(sessionId)
        services.playbackEngine.loadSession(sessionId, samples, markers)
        services.playbackEngine.setSectorLinesGeoJson(config?.sectorLinesGeoJson)
        services.playbackEngine.clearGhost()
        services.videoSyncEngine.bindSession(sessionId)
    }

    suspend fun loadWithGhost(
        services: ExpeditionGaugeServices,
        primarySessionId: Long,
        ghostSessionId: Long,
    ) {
        val primarySamples = services.database.sampleDao().getBySession(primarySessionId)
        val ghostSamples = services.database.sampleDao().getBySession(ghostSessionId)
        val primaryConfig = services.lapTimingService.getTrackConfig(primarySessionId)
        val ghostConfig = services.lapTimingService.getTrackConfig(ghostSessionId)
        val alertTimestamps = loadAlertTimestamps(services, primarySessionId)
        val markTimestamps = loadMarkEventTimestamps(services, primarySessionId)
        val markers = PlaybackEngine.computeMarkers(primarySamples, alertTimestamps, markTimestamps)
        services.playbackEngine.loadSession(primarySessionId, primarySamples, markers)
        services.playbackEngine.setSectorLinesGeoJson(primaryConfig?.sectorLinesGeoJson)
        val overlay = GhostLapOverlay().buildState(
            primary = primarySamples,
            ghost = ghostSamples,
            primaryStartFinishGeoJson = primaryConfig?.startFinishGeoJson,
            ghostStartFinishGeoJson = ghostConfig?.startFinishGeoJson,
        )
        if (overlay.trackMismatch) {
            services.playbackEngine.setGhostTrackMismatch(true)
        } else {
            services.playbackEngine.loadGhost(overlay.ghostSamples)
        }
        services.playbackEngine.setShowGhost(true)
    }
}
