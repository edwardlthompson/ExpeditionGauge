package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.dao.SessionEventDao
import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.markeventvoice.MarkEventVoice
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.json.JSONObject

object SessionEventFactory {
    fun fromSnapshot(
        sessionId: Long,
        snapshot: TelemetrySnapshot,
        tag: String? = null,
        audioUri: String? = null,
    ): SessionEventEntity {
        val json = JSONObject()
            .put("timestampMs", snapshot.timestampMs)
            .put("speedMps", snapshot.speedMps.toDouble())
            .put("latG", snapshot.latG.toDouble())
            .put("pitchDeg", snapshot.pitchDeg.toDouble())
            .put("rollDeg", snapshot.rollDeg.toDouble())
            .apply {
                snapshot.driftAngleDeg?.let { put("betaDeg", it.toDouble()) }
                snapshot.slipRatio?.let { put("slipRatio", it.toDouble()) }
                snapshot.throttlePct?.let { put("throttlePct", it.toDouble()) }
                snapshot.rpm?.let { put("rpm", it.toDouble()) }
                tag?.let { put("tag", it) }
            }
        val payload = audioUri?.let { MarkEventVoice.withAudioUri(json.toString(), it) } ?: json.toString()
        return SessionEventEntity(
            sessionId = sessionId,
            timestampMs = snapshot.timestampMs,
            eventType = "mark",
            payloadJson = payload,
        )
    }
}

class SessionEventRecorder(
    private val sessionEventDao: SessionEventDao,
) {
    suspend fun markEvent(
        sessionId: Long,
        snapshot: TelemetrySnapshot,
        tag: String? = null,
        audioUri: String? = null,
    ): Long {
        return sessionEventDao.insert(SessionEventFactory.fromSnapshot(sessionId, snapshot, tag, audioUri))
    }
}
