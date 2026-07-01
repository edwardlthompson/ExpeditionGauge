package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GBallTrailBufferDedupeTest {
    @Test
    fun add_skipsSamplesCloserThanThreshold() {
        val buffer = GBallTrailBuffer(capacity = 5)
        buffer.add(0f, 0f)
        buffer.add(0.01f, 0.01f)
        assertEquals(1, buffer.snapshot().size)
        buffer.add(0.1f, 0.1f)
        assertEquals(2, buffer.snapshot().size)
    }
}
