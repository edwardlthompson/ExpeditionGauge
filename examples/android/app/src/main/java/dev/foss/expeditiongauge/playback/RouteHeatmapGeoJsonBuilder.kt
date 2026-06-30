package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

internal object RouteHeatmapGeoJsonBuilder {
    fun buildHeatmapGeoJson(samples: List<SampleEntity>, metric: HeatmapMetric): String {
        val points = RouteGeoJsonBuilder.decimatedSamples(samples)
        val features = buildString {
            for (i in 0 until points.lastIndex) {
                val start = points[i]
                val end = points[i + 1]
                val lat1 = start.latitude ?: continue
                val lon1 = start.longitude ?: continue
                val lat2 = end.latitude ?: continue
                val lon2 = end.longitude ?: continue
                val intensity = maxOf(
                    RouteHeatmapLayer.sampleIntensity(start, metric),
                    RouteHeatmapLayer.sampleIntensity(end, metric),
                )
                val colorBucket = RouteHeatmapLayer.heatmapColorBucket(intensity, metric)
                if (isNotEmpty()) append(',')
                append(
                    """{"type":"Feature","geometry":{"type":"LineString","coordinates":""" +
                        """[[$lon1,$lat1],[$lon2,$lat2]]},"properties":{""" +
                        """"colorBucket":$colorBucket,"heatmapIntensity":$intensity}}""",
                )
            }
        }
        return """{"type":"FeatureCollection","features":[$features]}"""
    }
}
