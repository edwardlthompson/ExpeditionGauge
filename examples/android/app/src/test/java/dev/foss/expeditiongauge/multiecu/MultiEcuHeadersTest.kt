package dev.foss.expeditiongauge.multiecu

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MultiEcuHeadersTest {
    @Test
    fun atshAndPresence() {
        assertEquals("ATSH7E0", MultiEcuHeaders.atsh("7E0"))
        assertTrue(MultiEcuHeaders.present("41 00 BE 1F A8 13"))
        assertFalse(MultiEcuHeaders.present("NO DATA"))
        assertFalse(MultiEcuHeaders.present("7F 01 12"))
    }

    @Test
    fun lineSkipsFunctionalOnly() {
        assertNull(MultiEcuHeaders.line(listOf("7DF")))
        assertEquals("ECU 7E0 · 7E1", MultiEcuHeaders.line(listOf("7DF", "7E0", "7E1")))
        assertTrue(MultiEcuHeaders.matches("ECU 7E0"))
        assertTrue(MultiEcuHeaders.summary().contains("7E0 ECM"))
    }
}
