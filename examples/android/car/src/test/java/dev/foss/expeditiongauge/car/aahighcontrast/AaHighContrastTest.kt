package dev.foss.expeditiongauge.car.aahighcontrast

import dev.foss.expeditiongauge.car.gauge.DriveHudTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AaHighContrastTest {
    @Test
    fun offUsesStandardNightAndDayTokens() {
        assertEquals(DriveHudTheme.DARK, AaHighContrast.theme(dark = true, enabled = false))
        assertEquals(DriveHudTheme.LIGHT, AaHighContrast.theme(dark = false, enabled = false))
    }

    @Test
    fun onUsesMaxContrastTokens() {
        val dark = AaHighContrast.theme(dark = true, enabled = true)
        assertEquals(0xFF000000.toInt(), dark.background)
        assertEquals(0xFFFFFFFF.toInt(), dark.primaryText)
        assertEquals(0xFFFFFF00.toInt(), dark.secondaryText)
        assertNotEquals(DriveHudTheme.DARK.background, dark.background)
        val light = AaHighContrast.theme(dark = false, textScale = 1.25f, enabled = true)
        assertEquals(0xFFFFFFFF.toInt(), light.background)
        assertEquals(0xFF000000.toInt(), light.primaryText)
        assertEquals(1.25f, light.textScale, 0.001f)
    }
}
