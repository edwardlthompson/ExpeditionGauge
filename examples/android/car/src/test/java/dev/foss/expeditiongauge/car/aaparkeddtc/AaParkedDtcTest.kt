package dev.foss.expeditiongauge.car.aaparkeddtc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaParkedDtcTest {
    @Test
    fun opensOnlyWhenParkedWithCodes() {
        assertFalse(AaParkedDtc.canOpen(parked = true, count = 0))
        assertFalse(AaParkedDtc.canOpen(parked = false, count = 2))
        assertTrue(AaParkedDtc.canOpen(parked = true, count = 1))
        val rows = AaParkedDtc.rows(
            listOf("P0420" to "Catalyst", "P0300" to "", "P0171" to "Lean"),
        )
        assertEquals(3, rows.size)
        assertEquals("P0420", rows[0].title)
        assertEquals("Stored DTC", rows[1].text)
        val many = AaParkedDtc.rows((1..8).map { "P000$it" to "x" })
        assertEquals(AaParkedDtc.MAX_ROWS, many.size)
    }
}
