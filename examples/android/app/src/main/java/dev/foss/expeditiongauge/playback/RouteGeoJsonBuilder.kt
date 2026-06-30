package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

data class RouteBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
) {
    val isValid: Boolean
        get() = minLat <= maxLat && minLon <= maxLon

    companion object {
        val Empty = RouteBounds(0.0, 0.0, 0.0, 0.0)
    }
}

object RouteGeoJsonBuilder {
    private const val MAX_SEGMENTS = 800

    fun decimatedSamples(samples: List<SampleEntity>): List<SampleEntity> {
        val withGps = samples.filter { it.latitude != null && it.longitude != null }
        if (withGps.size <= MAX_SEGMENTS) return withGps
        val step = (withGps.size / MAX_SEGMENTS).coerceAtLeast(1)
        return withGps.filterIndexed { index, _ -> index % step == 0 }
    }

    fun buildRouteGeoJson(samples: List<SampleEntity>): String {
        val points = decimatedSamples(samples)
        val features = buildString {
            for (i in 0 until points.lastIndex) {
                val start = points[i]
                val end = points[i + 1]
                val lat1 = start.latitude ?: continue
                val lon1 = start.longitude ?: continue
                val lat2 = end.latitude ?: continue
                val lon2 = end.longitude ?: continue
                val beta = end.driftAngleDeg ?: start.driftAngleDeg
                val lonAccel = (start.lonAccel + end.lonAccel) / 2f
                val latG = maxOf(kotlin.math.abs(start.latG), kotlin.math.abs(end.latG))
                val slip = listOfNotNull(start.slipRatio, end.slipRatio).maxOrNull()
                val colorBucket = DriftRouteStyling.colorBucket(beta, lonAccel)
                val widthBucket = DriftRouteStyling.widthBucket(latG)
                val slipAlpha = DriftRouteStyling.slipOverlayAlpha(slip)
                if (isNotEmpty()) append(',')
                append(
                    """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                        """[[$lon1,$lat1],[$lon2,$lat2]]},"properties":{""" +
                        """"colorBucket":$colorBucket,"widthBucket":$widthBucket,""" +
                        """"slipAlpha":$slipAlpha,"driftAngle":${beta ?: 0f},""" +
                        """"lonAccel":$lonAccel,"latG":$latG""" +
                        (slip?.let { ""","slipRatio":$it""" } ?: "") +
                        "}}",
                )
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun buildVehicleGeoJson(sample: SampleEntity?): String {
        val lat = sample?.latitude
        val lon = sample?.longitude
        if (lat == null || lon == null) {
            return """{"type":"FeatureCollection","features":[]}"""
        }
        val bearing = (sample.bodyYawDeg ?: sample.headingDeg).toDouble()
        val beta = (sample.driftAngleDeg ?: 0f).toDouble()
        return """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[$lon,$lat]},"properties":{"bearing":$bearing,"beta":$beta}}]}"""
    }

    fun buildSlipGeoJson(samples: List<SampleEntity>): String {
        val points = decimatedSamples(samples)
        val features = buildString {
            for (i in 0 until points.lastIndex) {
                val start = points[i]
                val end = points[i + 1]
                val slip = listOfNotNull(start.slipRatio, end.slipRatio).maxOrNull() ?: continue
                if (slip < 0.08f) continue
                val lat1 = start.latitude ?: continue
                val lon1 = start.longitude ?: continue
                val lat2 = end.latitude ?: continue
                val lon2 = end.longitude ?: continue
                val slipAlpha = DriftRouteStyling.slipOverlayAlpha(slip)
                if (isNotEmpty()) append(',')
                append(
                    """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                        """[[$lon1,$lat1],[$lon2,$lat2]]},"properties":{""" +
                        """"slipRatio":$slip,"slipAlpha":$slipAlpha}}""",
                )
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun buildHeatmapGeoJson(samples: List<SampleEntity>, metric: HeatmapMetric): String =
        RouteHeatmapGeoJsonBuilder.buildHeatmapGeoJson(samples, metric)

    fun buildGhostRouteGeoJson(samples: List<SampleEntity>): String {
        val points = decimatedSamples(samples)
        val features = buildString {
            for (i in 0 until points.lastIndex) {
                val start = points[i]
                val end = points[i + 1]
                val lat1 = start.latitude ?: continue
                val lon1 = start.longitude ?: continue
                val lat2 = end.latitude ?: continue
                val lon2 = end.longitude ?: continue
                if (isNotEmpty()) append(',')
                append(
                    """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                        """[[$lon1,$lat1],[$lon2,$lat2]]},"properties":{}}""",
                )
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun bounds(samples: List<SampleEntity>): RouteBounds {
        val coords = samples.mapNotNull { s ->
            val lat = s.latitude ?: return@mapNotNull null
            val lon = s.longitude ?: return@mapNotNull null
            lat to lon
        }
        if (coords.isEmpty()) return RouteBounds.Empty
        return RouteBounds(
            minLat = coords.minOf { it.first },
            maxLat = coords.maxOf { it.first },
            minLon = coords.minOf { it.second },
            maxLon = coords.maxOf { it.second },
        )
    }
}
