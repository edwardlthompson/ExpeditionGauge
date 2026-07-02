package dev.foss.expeditiongauge.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenKeepAwakeTest {
    @Test
    fun enabledPreferenceKeepsScreenAwake() {
        assertTrue(shouldKeepScreenAwake(true))
    }

    @Test
    fun disabledPreferenceAllowsTimeout() {
        assertFalse(shouldKeepScreenAwake(false))
    }
}
