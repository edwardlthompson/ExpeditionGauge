package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.gpxbeta.GpxBetaExtensions
import dev.foss.expeditiongauge.recording.SessionMetadata
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object ExportFormatters {
    private val BASE_CSV_COLUMNS = listOf(
        "timestampMs", "lat", "lon", "alt", "speedMps", "headingDeg",
        "pitchDeg", "rollDeg", "latG", "lonAccel", "driftAngleDeg", "bodyYawDeg",
        "velocityHeadingDeg", "slipRatio", "rpm", "throttle", "load", "extrasJson",
    )
    private val TPMS_CSV_COLUMNS = listOf(
        "tpms_fl_kpa", "tpms_fr_kpa", "tpms_rl_kpa", "tpms_rr_kpa",
        "tpms_fl_temp_c", "tpms_fr_temp_c", "tpms_rl_temp_c", "tpms_rr_temp_c",
    )

    fun toCsv(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
        val includeTpms = ExportExtrasParser.sessionHasTpms(samples)
        val header = buildList {
            addAll(BASE_CSV_COLUMNS)
            if (includeTpms) addAll(TPMS_CSV_COLUMNS)
        }.joinToString(",")
        val rows = samples.map { s ->
            val tpms = ExportExtrasParser.tpmsColumns(s.extrasJson)
            val base = listOf(
                s.timestampMs, s.latitude, s.longitude, s.altitudeM, s.speedMps, s.headingDeg,
                s.pitchDeg, s.rollDeg, s.latG, s.lonAccel, s.driftAngleDeg, s.bodyYawDeg,
                s.velocityHeadingDeg, s.slipRatio, s.rpm, s.throttle, s.load,
                quote(s.extrasJson ?: ""),
            )
            val tpmsCols = if (includeTpms) {
                listOf(
                    tpms.frontLeft.pressureKpa, tpms.frontRight.pressureKpa,
                    tpms.rearLeft.pressureKpa, tpms.rearRight.pressureKpa,
                    tpms.frontLeft.tempC, tpms.frontRight.tempC,
                    tpms.rearLeft.tempC, tpms.rearRight.tempC,
                )
            } else {
                emptyList()
            }
            (base + tpmsCols).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    fun toJson(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
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
                    val tpms = ExportExtrasParser.tpmsColumns(s.extrasJson)
                    put(JSONObject().apply {
                        put("timestampMs", s.timestampMs)
                        put("lat", s.latitude)
                        put("lon", s.longitude)
                        put("speedMps", s.speedMps)
                        put("driftAngleDeg", s.driftAngleDeg)
                        put("bodyYawDeg", s.bodyYawDeg)
                        put("velocityHeadingDeg", s.velocityHeadingDeg)
                        put("slipRatio", s.slipRatio)
                        if (tpms.hasAnyData) {
                            put("tpms", tpmsJson(tpms))
                        }
                        put("extrasJson", s.extrasJson)
                    })
                }
            })
        }
        return root.toString(2)
    }

    fun toGpx(session: RecordingSessionEntity, samples: List<SampleEntity>): String {
        val points = samples.filter { it.latitude != null && it.longitude != null }.joinToString("\n") { s ->
            val extensions = buildString {
                append("        <extensions>\n")
                append(GpxBetaExtensions.tags(s.latG, s.lonAccel, s.driftAngleDeg))
                s.slipRatio?.let { append("          <slipRatio>$it</slipRatio>\n") }
                s.rpm?.let { append("          <rpm>$it</rpm>\n") }
                s.throttle?.let { append("          <throttlePct>$it</throttlePct>\n") }
                s.bodyYawDeg?.let { append("          <bodyYawDeg>$it</bodyYawDeg>\n") }
                s.velocityHeadingDeg?.let { append("          <velocityHeadingDeg>$it</velocityHeadingDeg>\n") }
                val tpms = ExportExtrasParser.tpmsColumns(s.extrasJson)
                if (tpms.hasAnyData) {
                    tpms.frontLeft.pressureKpa?.let { append("          <tpmsFlKpa>$it</tpmsFlKpa>\n") }
                    tpms.frontRight.pressureKpa?.let { append("          <tpmsFrKpa>$it</tpmsFrKpa>\n") }
                    tpms.rearLeft.pressureKpa?.let { append("          <tpmsRlKpa>$it</tpmsRlKpa>\n") }
                    tpms.rearRight.pressureKpa?.let { append("          <tpmsRrKpa>$it</tpmsRrKpa>\n") }
                }
                append("        </extensions>\n")
            }
            """      <trkpt lat="${s.latitude}" lon="${s.longitude}">
        <ele>${s.altitudeM ?: 0}</ele>
        <time>${isoTime(s.timestampMs)}</time>
$extensions      </trkpt>"""
        }
        return """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="ExpeditionGauge" ${GpxBetaExtensions.xmlnsAttr()}>
  <trk>
    <name>${session.name}</name>
    <trkseg>
$points
    </trkseg>
  </trk>
</gpx>"""
    }

    private fun tpmsJson(tpms: TpmsExportColumns): JSONObject = JSONObject().apply {
        put("fl", cornerJson(tpms.frontLeft))
        put("fr", cornerJson(tpms.frontRight))
        put("rl", cornerJson(tpms.rearLeft))
        put("rr", cornerJson(tpms.rearRight))
    }

    private fun cornerJson(corner: TpmsCornerExport): JSONObject = JSONObject().apply {
        corner.pressureKpa?.let { put("pressureKpa", it) }
        corner.tempC?.let { put("tempC", it) }
    }

    private fun quote(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    private fun isoTime(ms: Long): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(ms))
}
