package dev.foss.expeditiongauge.phonehuddtc

import dev.foss.expeditiongauge.obd.dtc.DtcCarousel
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneHudDtcTest {
    @Test
    fun emptyListHidesFooter() {
        assertNull(PhoneHudDtc.line(emptyList(), 0L))
    }

    @Test
    fun lineMatchesCarouselFrame() {
        val entries = listOf(
            DtcEntry("P0420", "Catalyst"),
            DtcEntry("P0171", "Lean"),
        )
        val expected = DtcCarousel.frame(entries, DtcCarousel.DWELL_MS)!!.line()
        assertEquals(expected, PhoneHudDtc.line(entries, DtcCarousel.DWELL_MS))
        assertTrue(PhoneHudDtc.line(entries, 0L)!!.contains("P0420"))
    }

    @Test
    fun fullTitleKeepsUntruncatedDescription() {
        val long = "A".repeat(80)
        val entry = DtcEntry("P0420", long)
        assertEquals("P0420 $long", PhoneHudDtc.fullTitle(entry))
        assertEquals(entry, PhoneHudDtc.current(listOf(entry), 0L))
    }
}
