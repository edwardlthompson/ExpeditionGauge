package dev.foss.expeditiongauge.keepawake

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeepAwakeMovingTest {
    @Test
    fun keepsAwakeOnlyWhenPrefOnAndMoving() {
        assertTrue(KeepAwakeMoving.moving(8f))
        assertFalse(KeepAwakeMoving.moving(0.1f))
        assertFalse(KeepAwakeMoving.moving(null))
        assertFalse(KeepAwakeMoving.parked(8f))
        assertTrue(KeepAwakeMoving.parked(0.1f))
        assertTrue(KeepAwakeMoving.parked(null))
        assertTrue(KeepAwakeMoving.shouldKeep(true, 8f))
        assertFalse(KeepAwakeMoving.shouldKeep(true, 0.1f))
        assertFalse(KeepAwakeMoving.shouldKeep(true, null))
        assertFalse(KeepAwakeMoving.shouldKeep(false, 8f))
    }
}
