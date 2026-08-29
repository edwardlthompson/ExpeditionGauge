package dev.foss.expeditiongauge.car.aaa11y

import org.junit.Assert.assertEquals
import org.junit.Test

class AaA11yTypeTest {
    @Test
    fun scalesTypeAndBuildsSpokenLabels() {
        assertEquals(1f, AaA11yType.scale(1f), 0.001f)
        assertEquals(1.25f, AaA11yType.scale(1f, largeTextPref = true), 0.001f)
        assertEquals(1.5f, AaA11yType.scale(2f), 0.001f)
        assertEquals(1f, AaA11yType.scale(0.8f), 0.001f)
        assertEquals("Speed, 72", AaA11yType.spoken("Speed", "72"))
        assertEquals("Drive HUD", AaA11yType.spoken("Drive HUD", "\u200B"))
    }
}
