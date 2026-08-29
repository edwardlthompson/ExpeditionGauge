package dev.foss.expeditiongauge.i18nlayoutstress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class I18nLayoutStressTest {
    @Test
    fun flagsLongGerman() {
        val de = "Offline-Karten herunterladen"
        assertTrue(I18nLayoutStress.overflows(de, I18nLayoutStress.HUD_MAX))
        assertEquals(de, I18nLayoutStress.longest(listOf("About", de)))
    }
}
