package dev.foss.expeditiongauge.obd.dtc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DtcCarouselTest {
    private val entries = listOf(
        DtcEntry("P0420", "Catalyst"),
        DtcEntry("P0300", "Misfire"),
        DtcEntry("C0035", "Wheel speed"),
    )

    @Test
    fun empty_hidesFooter() {
        assertNull(DtcCarousel.frame(emptyList(), 0L))
    }

    @Test
    fun dwellAdvancesIndex() {
        val f0 = DtcCarousel.frame(entries, 0L)!!
        assertEquals(0, f0.index)
        assertEquals("1/3", f0.label)
        assertEquals("P0420", f0.code)

        val f1 = DtcCarousel.frame(entries, DtcCarousel.DWELL_MS)!!
        assertEquals(1, f1.index)
        assertEquals("2/3", f1.label)
        assertEquals("P0300", f1.code)

        val f2 = DtcCarousel.frame(entries, DtcCarousel.DWELL_MS * 2 + 100)!!
        assertEquals(2, f2.index)
        assertEquals("3/3", f2.label)
    }

    @Test
    fun lineIncludesCounterAndCode() {
        val line = DtcCarousel.frame(entries, 0L)!!.line()
        assertTrue(line.startsWith("1/3  P0420"))
        assertTrue(line.contains("Catalyst"))
    }

    @Test
    fun truncateEllipsis_whenWide() {
        val measure: (String) -> Float = { it.length * 10f }
        assertEquals("Hello", DtcCarousel.truncateEllipsis("Hello", 100f, measure))
        val truncated = DtcCarousel.truncateEllipsis("Hello world", 50f, measure)
        assertTrue(truncated.endsWith("…"))
        assertTrue(measure(truncated) <= 50f)
    }
}
