package dev.foss.expeditiongauge.storagemeter

import org.junit.Assert.assertEquals
import org.junit.Test

class StorageMeterTest {
    @Test
    fun percentUsedClampsAndHandlesZeroCap() {
        assertEquals(0, StorageMeter.percentUsed(0, 100))
        assertEquals(50, StorageMeter.percentUsed(50, 100))
        assertEquals(100, StorageMeter.percentUsed(200, 100))
        assertEquals(100, StorageMeter.percentUsed(10, 0))
    }
}
