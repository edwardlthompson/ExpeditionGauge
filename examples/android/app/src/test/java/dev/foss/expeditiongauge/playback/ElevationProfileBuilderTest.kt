package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ElevationProfileBuilderTest {
    @Test
    fun build_returnsNull_whenFewAltitudes() {
        val samples = listOf(
            sample(0, 100.0),
            sample(1, null),
        )
        assertNull(ElevationProfileBuilder.build(samples))
    }

    @Test
    fun build_computesAscentDescent() {
        val samples = listOf(
            sample(0, 100.0),
            sample(1, 110.0),
            sample(2, 105.0),
            sample(3, 120.0),
        )
        val profile = ElevationProfileBuilder.build(samples)
        assertNotNull(profile)
        assertEquals(100.0, profile!!.minM, 0.01)
        assertEquals(120.0, profile.maxM, 0.01)
        assertEquals(25.0, profile.totalAscentM, 0.01)
        assertEquals(5.0, profile.totalDescentM, 0.01)
        assertEquals(4, profile.sampleCount)
    }

    @Test
    fun smooth_reducesNoise() {
        val noisy = listOf(100.0, 120.0, 101.0, 119.0, 100.0)
        val smoothed = ElevationProfileBuilder.smooth(noisy, 3)
        assertEquals(noisy.size, smoothed.size)
        assert(smoothed[2] in 100.0..120.0)
    }

    @Test
    fun fillMissing_interpolatesGaps() {
        val filled = ElevationProfileBuilder.fillMissing(listOf(100.0, null, 120.0))
        assertEquals(3, filled.size)
        assertEquals(110.0, filled[1], 0.01)
    }

    private fun sample(index: Int, alt: Double?): SampleEntity = SampleEntity(
        id = index.toLong(),
        sessionId = 1L,
        timestampMs = index * 1000L,
        latitude = 0.0,
        longitude = 0.0,
        altitudeM = alt,
        speedMps = 0f,
        headingDeg = 0f,
        latG = 0f,
    )
}
