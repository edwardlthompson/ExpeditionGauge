package dev.foss.expeditiongauge.hapticalerts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HapticOverLimitTest {
    @Test
    fun vibratesOnlyWhenEnabledAndOverLimit() {
        assertTrue(HapticOverLimit.shouldVibrate(enabled = true, overLimit = true))
        assertFalse(HapticOverLimit.shouldVibrate(enabled = false, overLimit = true))
        assertFalse(HapticOverLimit.shouldVibrate(enabled = true, overLimit = false))
        assertFalse(HapticOverLimit.shouldVibrate(enabled = false, overLimit = false))
    }
}
