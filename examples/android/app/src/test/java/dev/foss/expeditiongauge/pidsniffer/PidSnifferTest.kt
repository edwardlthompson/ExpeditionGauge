package dev.foss.expeditiongauge.pidsniffer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PidSnifferTest {
    @Test
    fun normalizeAllowsMode01AndBlocksVinClear() {
        assertEquals("010C", PidSniffer.normalize("01 0c"))
        assertNull(PidSniffer.normalize("04"))
        assertNull(PidSniffer.normalize("0902"))
        assertNull(PidSniffer.normalize("1"))
    }

    @Test
    fun sanitizeRedactsVinAndTruncates() {
        assertEquals("VIN redacted", PidSniffer.sanitize("0100", "49 02 01 31 46"))
        assertEquals("NO DATA", PidSniffer.sanitize("010C", null))
        assertEquals("blocked", PidSniffer.sanitize("04", "44"))
        val long = "41".repeat(80)
        assertEquals(PidSniffer.MAX_RAW, PidSniffer.sanitize("010C", long).length)
    }
}
