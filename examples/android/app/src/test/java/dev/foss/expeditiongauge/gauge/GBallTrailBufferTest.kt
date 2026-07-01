package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GBallTrailBufferTest {
    @Test
    fun addAndSnapshotInOrder() {
        val buf = GBallTrailBuffer(capacity = 3)
        buf.add(0.1f, 0.2f)
        buf.add(0.3f, 0.4f)
        val snap = buf.snapshot()
        assertEquals(2, snap.size)
        assertEquals(0.1f, snap[0].first, 0.001f)
        assertEquals(0.3f, snap[1].first, 0.001f)
    }

    @Test
    fun wrapsOldestWhenFull() {
        val buf = GBallTrailBuffer(capacity = 2)
        buf.add(1f, 0f)
        buf.add(2f, 0f)
        buf.add(3f, 0f)
        val snap = buf.snapshot()
        assertEquals(2, snap.size)
        assertEquals(2f, snap[0].first, 0.001f)
        assertEquals(3f, snap[1].first, 0.001f)
    }

    @Test
    fun clearEmptiesBuffer() {
        val buf = GBallTrailBuffer()
        buf.add(1f, 1f)
        buf.clear()
        assertTrue(buf.snapshot().isEmpty())
    }
}
