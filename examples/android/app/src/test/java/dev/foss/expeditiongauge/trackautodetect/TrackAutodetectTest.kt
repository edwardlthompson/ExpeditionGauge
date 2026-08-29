package dev.foss.expeditiongauge.trackautodetect

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackAutodetectTest {
    @Test
    fun detectsClosedLoopAndBuildsGate() {
        val samples = loopSamples()
        val found = TrackAutodetect.detect(samples)
        assertNotNull(found)
        val json = TrackAutodetect.startFinishGeoJson(samples)
        assertNotNull(json)
        assertTrue(json!!.contains("LineString"))
    }

    @Test
    fun rejectsShortOrOpenPath() {
        val shortPath = listOf(
            sample(0.0, 0.0),
            sample(0.0, 0.0001),
        )
        assertNull(TrackAutodetect.detect(shortPath))
        val open = (0..12).map { i -> sample(0.0, i * 0.001) }
        assertNull(TrackAutodetect.detect(open))
    }

    private fun loopSamples(): List<SampleEntity> {
        val lons = listOf(0.0, 0.001, 0.002, 0.003, 0.002, 0.001, 0.0, 0.0)
        return lons.mapIndexed { index, lon -> sample(0.0, lon, index) }
    }

    private fun sample(lat: Double, lon: Double, index: Int = 0): SampleEntity =
        SampleEntity(
            sessionId = 1,
            timestampMs = index * 1000L,
            latitude = lat,
            longitude = lon,
            headingDeg = 90f,
        )
}
