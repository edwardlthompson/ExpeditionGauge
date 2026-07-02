package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteGeoJsonBuilderTest {
    @Test
    fun boundsFromGpsSamples() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latitude = 47.0, longitude = -122.0),
            SampleEntity(id = 2, sessionId = 1, timestampMs = 100, latitude = 47.1, longitude = -121.9),
        )
        val bounds = RouteGeoJsonBuilder.bounds(samples)
        assertEquals(47.0, bounds.minLat, 0.001)
        assertEquals(47.1, bounds.maxLat, 0.001)
        assertEquals(-122.0, bounds.minLon, 0.001)
        assertEquals(-121.9, bounds.maxLon, 0.001)
    }

    @Test
    fun routeGeoJsonIncludesColorBucket() {
        val samples = listOf(
            SampleEntity(
                id = 1,
                sessionId = 1,
                timestampMs = 0,
                latitude = 47.0,
                longitude = -122.0,
                lonAccel = -0.5f,
            ),
            SampleEntity(
                id = 2,
                sessionId = 1,
                timestampMs = 100,
                latitude = 47.01,
                longitude = -121.99,
                lonAccel = -0.5f,
            ),
        )
        val json = RouteGeoJsonBuilder.buildRouteGeoJson(samples)
        assertTrue(json.contains("\"colorBucket\":${DrivingRouteStyling.BRAKE_BUCKET}"))
    }
}
