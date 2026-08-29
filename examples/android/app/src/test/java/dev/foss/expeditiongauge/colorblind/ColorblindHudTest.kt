package dev.foss.expeditiongauge.colorblind

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ColorblindHudTest {
    @Test
    fun cycleAndSafeAlertColors() {
        assertEquals(ColorblindHudMode.NONE, ColorblindHud.parse(null))
        assertEquals(ColorblindHudMode.DEUTERANOPIA, ColorblindHud.cycle(ColorblindHudMode.NONE))
        assertEquals(ColorblindHudMode.NONE, ColorblindHud.cycle(ColorblindHudMode.TRITANOPIA))
        assertNotEquals(
            ColorblindHud.alertRed(ColorblindHudMode.NONE),
            ColorblindHud.alertRed(ColorblindHudMode.DEUTERANOPIA),
        )
        assertEquals(0xFF0072B2, ColorblindHud.alertRed(ColorblindHudMode.PROTANOPIA))
        assertEquals(0xFFF0E442, ColorblindHud.alertYellow(ColorblindHudMode.TRITANOPIA))
        assertEquals("Deuteranopia", ColorblindHud.label(ColorblindHudMode.DEUTERANOPIA))
    }
}
