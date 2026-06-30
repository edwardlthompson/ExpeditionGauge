package dev.foss.expeditiongauge.timing

import kotlin.math.cos
import kotlin.math.sin

object TrackLineBuilder {
    private const val METERS_PER_DEG_LAT = 111_320.0

    fun perpendicularLine(
        lat: Double,
        lon: Double,
        headingDeg: Float,
        widthM: Double = 30.0,
    ): LineSegment {
        val perpRad = Math.toRadians(headingDeg.toDouble() + 90.0)
        val half = widthM / 2.0
        val dLat = half * cos(perpRad) / METERS_PER_DEG_LAT
        val dLon = half * sin(perpRad) / (METERS_PER_DEG_LAT * cos(Math.toRadians(lat)))
        return LineSegment(lat - dLat, lon - dLon, lat + dLat, lon + dLon)
    }

    fun toStartFinishGeoJson(line: LineSegment): String =
        """{"type":"LineString","coordinates":[[${line.startLon},${line.startLat}],[${line.endLon},${line.endLat}]]}"""

    fun appendSectorLine(existing: String?, line: LineSegment): String {
        val lines = parseSectorLinesFromGeoJson(existing).toMutableList()
        if (lines.size >= 9) return existing.orEmpty()
        lines += line
        val segments = lines.joinToString(",") { seg ->
            """[[${seg.startLon},${seg.startLat}],[${seg.endLon},${seg.endLat}]]"""
        }
        return """{"type":"MultiLineString","coordinates":[$segments]}"""
    }

    fun sectorCount(geoJson: String?): Int = parseSectorLinesFromGeoJson(geoJson).size
}
