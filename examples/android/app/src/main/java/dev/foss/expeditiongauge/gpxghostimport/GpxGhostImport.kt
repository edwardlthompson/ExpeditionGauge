package dev.foss.expeditiongauge.gpxghostimport

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.playback.PlaybackEngine

data class GhostFix(
    val latitude: Double,
    val longitude: Double,
    val timestampMs: Long = 0L,
)

/** Parse GPX trkpt or FIT lat,lon,ts lines into ghost samples. */
object GpxGhostImport {
    private val TRKPT = Regex("""<trkpt\s+lat="([-0-9.]+)"\s+lon="([-0-9.]+)"""")
    private val FIT = Regex("""^(-?[0-9.]+),(-?[0-9.]+)(?:,(\d+))?$""")

    fun parse(text: String): List<GhostFix> {
        val gpx = TRKPT.findAll(text).map { match ->
            GhostFix(match.groupValues[1].toDouble(), match.groupValues[2].toDouble())
        }.toList()
        if (gpx.isNotEmpty()) return gpx
        return text.lineSequence().mapNotNull { line ->
            val m = FIT.matchEntire(line.trim()) ?: return@mapNotNull null
            GhostFix(
                latitude = m.groupValues[1].toDouble(),
                longitude = m.groupValues[2].toDouble(),
                timestampMs = m.groupValues[3].toLongOrNull() ?: 0L,
            )
        }.toList()
    }

    fun applyTo(engine: PlaybackEngine, text: String) {
        engine.loadGhost(toSamples(parse(text)))
    }

    fun toSamples(fixes: List<GhostFix>, sessionId: Long = 0L): List<SampleEntity> =
        fixes.mapIndexed { index, fix ->
            SampleEntity(
                sessionId = sessionId,
                timestampMs = if (fix.timestampMs > 0L) fix.timestampMs else index * 100L,
                latitude = fix.latitude,
                longitude = fix.longitude,
            )
        }
}
