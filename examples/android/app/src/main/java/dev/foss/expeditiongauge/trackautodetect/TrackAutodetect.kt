package dev.foss.expeditiongauge.trackautodetect

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao
import dev.foss.expeditiongauge.data.db.dao.SampleDao
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.TrackLineBuilder
import dev.foss.expeditiongauge.timing.haversineM
import kotlinx.coroutines.flow.first

data class DetectedTrack(
    val latitude: Double,
    val longitude: Double,
    val headingDeg: Float,
    val loopMeters: Double,
)

/** Close a GPS loop and build a start/finish gate. */
object TrackAutodetect {
    const val CLOSE_M = 25.0
    const val MIN_TRAVEL_M = 200.0

    fun detect(
        samples: List<SampleEntity>,
        closeM: Double = CLOSE_M,
        minTravelM: Double = MIN_TRAVEL_M,
    ): DetectedTrack? {
        val points = samples.mapNotNull { sample ->
            val lat = sample.latitude ?: return@mapNotNull null
            val lon = sample.longitude ?: return@mapNotNull null
            Triple(lat, lon, sample.headingDeg)
        }
        if (points.size < 8) return null
        val start = points.first()
        var travel = 0.0
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            travel += haversineM(prev.first, prev.second, curr.first, curr.second)
            if (travel < minTravelM) continue
            val close = haversineM(start.first, start.second, curr.first, curr.second)
            if (close <= closeM) {
                return DetectedTrack(start.first, start.second, curr.third, travel)
            }
        }
        return null
    }

    fun startFinishGeoJson(samples: List<SampleEntity>): String? {
        val found = detect(samples) ?: return null
        val line = TrackLineBuilder.perpendicularLine(found.latitude, found.longitude, found.headingDeg)
        return TrackLineBuilder.toStartFinishGeoJson(line)
    }

    suspend fun loadLatestTrail(
        sessionDao: RecordingSessionDao,
        sampleDao: SampleDao,
    ): List<SampleEntity> {
        val latest = sessionDao.observeAll().first().firstOrNull() ?: return emptyList()
        return sampleDao.getBySession(latest.id)
    }

    fun loader(database: ExpeditionGaugeDatabase): suspend () -> List<SampleEntity> = {
        loadLatestTrail(database.recordingSessionDao(), database.sampleDao())
    }
}
