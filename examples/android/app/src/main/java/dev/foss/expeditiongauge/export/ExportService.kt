package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.recording.SessionMetadata
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExportFormat { CSV, JSON, GPX }

class ExportService(private val database: ExpeditionGaugeDatabase) {
    private val sessionDao = database.recordingSessionDao()
    private val sampleDao = database.sampleDao()

    suspend fun exportSession(sessionId: Long, format: ExportFormat, outputDir: File): File {
        val session = sessionDao.getById(sessionId)
            ?: throw IllegalArgumentException("Session $sessionId not found")
        val samples = sampleDao.getBySession(sessionId)
        outputDir.mkdirs()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(session.startTimeMs))
        val file = File(outputDir, "session_${sessionId}_$stamp.${format.name.lowercase()}")
        file.writeText(
            when (format) {
                ExportFormat.CSV -> toCsv(session, samples)
                ExportFormat.JSON -> toJson(session, samples)
                ExportFormat.GPX -> toGpx(session, samples)
            },
        )
        return file
    }

    private fun toCsv(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
        val header = listOf(
            "timestampMs", "lat", "lon", "alt", "speedMps", "headingDeg",
            "pitchDeg", "rollDeg", "latG", "lonAccel", "driftAngleDeg", "bodyYawDeg",
            "velocityHeadingDeg", "slipRatio", "rpm", "throttle", "load", "extrasJson",
        ).joinToString(",")
        val rows = samples.map { s ->
            listOf(
                s.timestampMs, s.latitude, s.longitude, s.altitudeM, s.speedMps, s.headingDeg,
                s.pitchDeg, s.rollDeg, s.latG, s.lonAccel, s.driftAngleDeg, s.bodyYawDeg,
                s.velocityHeadingDeg, s.slipRatio, s.rpm, s.throttle, s.load,
                quote(s.extrasJson ?: ""),
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun toJson(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
        val metadata = SessionMetadata.fromEntity(session)
        val root = JSONObject().apply {
            put("session", JSONObject().apply {
                put("id", session.id)
                put("name", session.name)
                put("startTimeMs", session.startTimeMs)
                put("endTimeMs", session.endTimeMs)
                put("recordingMode", session.recordingMode.name)
            })
            put("metadata", metadata.toExportJson())
            put("samples", JSONArray().apply {
                samples.forEach { s ->
                    put(JSONObject().apply {
                        put("timestampMs", s.timestampMs)
                        put("lat", s.latitude)
                        put("lon", s.longitude)
                        put("speedMps", s.speedMps)
                        put("driftAngleDeg", s.driftAngleDeg)
                        put("slipRatio", s.slipRatio)
                        put("extrasJson", s.extrasJson)
                    })
                }
            })
        }
        return root.toString(2)
    }

    private fun toGpx(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
        val points = samples.filter { it.latitude != null && it.longitude != null }.joinToString("\n") { s ->
            """      <trkpt lat="${s.latitude}" lon="${s.longitude}">
        <ele>${s.altitudeM ?: 0}</ele>
        <time>${isoTime(s.timestampMs)}</time>
      </trkpt>"""
        }
        return """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="ExpeditionGauge">
  <trk>
    <name>${session.name}</name>
    <trkseg>
$points
    </trkseg>
  </trk>
</gpx>"""
    }

    private fun quote(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    private fun isoTime(ms: Long): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(ms))
}
