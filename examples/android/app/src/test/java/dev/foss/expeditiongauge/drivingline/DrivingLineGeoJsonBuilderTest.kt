package dev.foss.expeditiongauge.drivingline

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class DrivingLineGeoJsonBuilderTest {
    @Test
    fun buildsMarkerAndOffsetGeoJson() {
        val samples = (0..3).map { i ->
            SampleEntity(
                id = i.toLong() + 1,
                sessionId = 1L,
                timestampMs = i * 100L,
                latG = if (i == 2) 1.0f else 0.2f,
                latitude = 0.0,
                longitude = i * 0.0001,
                lonAccel = if (i == 1) -0.5f else 0.1f,
            )
        }
        val analysis = DrivingLineAnalyzer().analyze(samples)
        val markers = DrivingLineGeoJsonBuilder.buildMarkersGeoJson(analysis)
        val bands = DrivingLineGeoJsonBuilder.buildOffsetBandsGeoJson(analysis, samples)
        assertTrue(markers.contains("apex") || markers.contains("brake"))
        assertTrue(bands.contains("FeatureCollection"))
    }
}
