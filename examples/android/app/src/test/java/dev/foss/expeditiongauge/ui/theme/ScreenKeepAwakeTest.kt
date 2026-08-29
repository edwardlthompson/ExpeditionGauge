package dev.foss.expeditiongauge.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenKeepAwakeTest {
    @Test
    fun enabledPreferenceKeepsScreenAwakeWhenMoving() {
        assertTrue(shouldKeepScreenAwake(true, 8f))
    }

    @Test
    fun enabledPreferenceAllowsTimeoutWhenParked() {
        assertFalse(shouldKeepScreenAwake(true, 0.1f))
        assertFalse(shouldKeepScreenAwake(true, null))
    }

    @Test
    fun disabledPreferenceAllowsTimeout() {
        assertFalse(shouldKeepScreenAwake(false, 8f))
    }
}
