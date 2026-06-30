package dev.foss.expeditiongauge.drivingline

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.parseSectorLinesFromGeoJson

object DrivingLineGeoJsonBuilder {
    fun buildMarkersGeoJson(analysis: DrivingLineAnalysis): String {
        val features = buildString {
            analysis.apexPoints.forEach { point ->
                appendMarkerFeature(point.longitude, point.latitude, "apex", point.latG)
            }
            analysis.brakeZones.forEach { point ->
                appendMarkerFeature(point.longitude, point.latitude, "brake", point.latG)
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun buildOffsetBandsGeoJson(
        analysis: DrivingLineAnalysis,
        samples: List<SampleEntity>,
    ): String {
        val features = buildString {
            analysis.offsetSegments.forEach { segment ->
                val start = samples.getOrNull(segment.startIndex) ?: return@forEach
                val end = samples.getOrNull(segment.endIndex) ?: return@forEach
                val lat1 = start.latitude ?: return@forEach
                val lon1 = start.longitude ?: return@forEach
                val lat2 = end.latitude ?: return@forEach
                val lon2 = end.longitude ?: return@forEach
                val bucket = offsetBucket(segment.offsetM)
                if (isNotEmpty()) append(',')
                append(
                    """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                        """[[$lon1,$lat1],[$lon2,$lat2]]},"properties":{""" +
                        """"offsetBucket":$bucket,"brakeIntensity":${segment.brakeIntensity}}}""",
                )
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun buildSectorBoundariesGeoJson(sectorLinesGeoJson: String?): String {
        val lines = parseSectorLinesFromGeoJson(sectorLinesGeoJson)
        if (lines.isEmpty()) return """{"type":"FeatureCollection","features":[]}"""
        val features = lines.joinToString(",") { line ->
            """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                """[[${line.startLon},${line.startLat}],[${line.endLon},${line.endLat}]]},"properties":{}}"""
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    private fun StringBuilder.appendMarkerFeature(
        lon: Double,
        lat: Double,
        markerType: String,
        latG: Float,
    ) {
        if (isNotEmpty()) append(',')
        append(
            """{"type":"Feature","geometry":{"type":"Point","coordinates":[$lon,$lat]},"properties":{""" +
                """"markerType":"$markerType","latG":$latG}}""",
        )
    }

    private fun offsetBucket(offsetM: Float): Int = when {
        kotlin.math.abs(offsetM) >= 1.5f -> 3
        kotlin.math.abs(offsetM) >= 1.0f -> 2
        kotlin.math.abs(offsetM) >= 0.5f -> 1
        else -> 0
    }
}
