package dev.foss.expeditiongauge.offlinetilecache

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineTileCacheTest {
    @Test
    fun evictsOldestWhenOverCap() {
        val keys = (1..10).map { "r$it" }
        val kept = OfflineTileCache.evictOldest(keys, max = 8)
        assertEquals(8, kept.size)
        assertEquals("r3", kept.first())
        assertEquals("r10", kept.last())
        assertTrue(OfflineTileCache.isOverCap(9))
        assertFalse(OfflineTileCache.isOverCap(8))
        assertEquals("2 / 8", OfflineTileCache.usageLabel(2))
    }
}
