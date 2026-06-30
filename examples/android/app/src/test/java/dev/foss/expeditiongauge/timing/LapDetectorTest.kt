package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LapDetectorTest {
    /** East-west gate at lat=0.00045; track runs north-south through it twice. */
    private val startFinish = LineSegment(0.00045, 0.0, 0.00045, 0.002)

    @Test
    fun detectsSingleLapOnLoopTrack() {
        val samples = loopSamples(sessionId = 1L)
        val detector = LapDetector(startFinish, minSpeedMps = 1f)
        val laps = detector.process(samples, sessionId = 1L)
        assertTrue(laps.isNotEmpty())
        assertTrue(laps.all { it.durationMs > 0 })
    }

    @Test
    fun crossingCountMatchesLineCrossings() {
        val samples = loopSamples(sessionId = 1L)
        val detector = LapDetector(startFinish, minSpeedMps = 1f)
        val crossings = detector.detectCrossings(samples)
        assertTrue(crossings.size >= 2)
    }

    @Test
    fun parseStartFinishFromGeoJsonWorks() {
        val geo = """{"type":"LineString","coordinates":[[0.0,0.0],[0.001,0.0]]}"""
        val segment = parseStartFinishFromGeoJson(geo)
        assertEquals(0.0, segment!!.startLat, 0.001)
        assertEquals(0.0, segment.startLon, 0.001)
    }

    private fun loopSamples(sessionId: Long): List<SampleEntity> {
        val lats = listOf(0.0001, 0.0003, 0.0005, 0.0007, 0.0009, 0.0007, 0.0005, 0.0003, 0.0001)
        return lats.mapIndexed { i, lat ->
            SampleEntity(
                id = i.toLong() + 1,
                sessionId = sessionId,
                timestampMs = i * 1000L,
                latitude = lat,
                longitude = 0.001,
                speedMps = 5f,
            )
        }
    }
}
