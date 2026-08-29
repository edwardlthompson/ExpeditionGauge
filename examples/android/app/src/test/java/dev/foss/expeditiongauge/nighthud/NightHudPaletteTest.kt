package dev.foss.expeditiongauge.nighthud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NightHudPaletteTest {
    @Test
    fun activeOnlyWhenNightAndEnabled() {
        assertTrue(NightHudPalette.active(nightBrightness = true, enabled = true))
        assertFalse(NightHudPalette.active(nightBrightness = true, enabled = false))
        assertFalse(NightHudPalette.active(nightBrightness = false, enabled = true))
        assertEquals(0xFFE09B3D, NightHudPalette.AMBER)
        assertEquals(0xFF000000, NightHudPalette.BACKGROUND)
    }
}
