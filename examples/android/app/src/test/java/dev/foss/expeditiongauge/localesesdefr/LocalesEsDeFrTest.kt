package dev.foss.expeditiongauge.localesesdefr

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalesEsDeFrTest {
    @Test
    fun supportsFirstLocales() {
        assertTrue(LocalesEsDeFr.supported("ES"))
        assertTrue(LocalesEsDeFr.supported("de"))
        assertTrue(LocalesEsDeFr.supported("fr"))
        assertFalse(LocalesEsDeFr.supported("ja"))
    }
}
