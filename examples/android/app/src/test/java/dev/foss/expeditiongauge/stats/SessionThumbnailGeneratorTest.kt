package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionThumbnailGeneratorTest {
    @Test
    fun generateReturnsEmptyWhenNoGps() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1, timestampMs = 0),
            SampleEntity(id = 2, sessionId = 1, timestampMs = 100),
        )
        assertTrue(SessionThumbnailGenerator.generate(samples).points.isEmpty())
    }

    @Test
    fun generateNormalizesRoutePolyline() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latitude = 0.0, longitude = 0.0),
            SampleEntity(id = 2, sessionId = 1, timestampMs = 100, latitude = 0.0, longitude = 1.0),
            SampleEntity(id = 3, sessionId = 1, timestampMs = 200, latitude = 1.0, longitude = 1.0),
        )
        val thumb = SessionThumbnailGenerator.generate(samples)
        assertEquals(3, thumb.points.size)
        assertEquals(0f, thumb.points.first().first, 0.001f)
        assertEquals(1f, thumb.points.last().first, 0.001f)
        assertEquals(0f, thumb.points.last().second, 0.001f)
    }

    @Test
    fun generateDecimatesLongRoutes() {
        val samples = (0 until 200).map { index ->
            SampleEntity(
                id = index.toLong(),
                sessionId = 1,
                timestampMs = index * 100L,
                latitude = index * 0.001,
                longitude = index * 0.002,
            )
        }
        val thumb = SessionThumbnailGenerator.generate(samples, maxPoints = 24)
        assertEquals(24, thumb.points.size)
    }
}
