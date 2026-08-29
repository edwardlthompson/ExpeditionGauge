package dev.foss.expeditiongauge.corneringhistogram

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CorneringHistogramTest {
    @Test
    fun binsAbsLatG() {
        val samples = listOf(
            SampleEntity(sessionId = 1, timestampMs = 1, latG = 0.1f),
            SampleEntity(sessionId = 1, timestampMs = 2, latG = -0.6f),
            SampleEntity(sessionId = 1, timestampMs = 3, latG = 0.6f),
        )
        val bins = CorneringHistogram.bins(samples)
        assertEquals(1, bins[0].count)
        assertEquals(2, bins[2].count)
    }
}
