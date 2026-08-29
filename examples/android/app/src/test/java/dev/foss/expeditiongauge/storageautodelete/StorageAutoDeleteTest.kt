package dev.foss.expeditiongauge.storageautodelete

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageAutoDeleteTest {
    @Test
    fun needsPruneWhenUsedMeetsCap() {
        assertTrue(StorageAutoDelete.needsPrune(usedBytes = 100, allowedBytes = 100))
        assertFalse(StorageAutoDelete.needsPrune(usedBytes = 99, allowedBytes = 100))
        assertFalse(StorageAutoDelete.needsPrune(usedBytes = 10, allowedBytes = 0))
    }

    @Test
    fun canDeleteOldestOnlyWhenUnprotectedAndOverCap() {
        assertTrue(StorageAutoDelete.canDeleteOldest(hasUnprotected = true, overCap = true))
        assertFalse(StorageAutoDelete.canDeleteOldest(hasUnprotected = false, overCap = true))
        assertFalse(StorageAutoDelete.canDeleteOldest(hasUnprotected = true, overCap = false))
    }
}
