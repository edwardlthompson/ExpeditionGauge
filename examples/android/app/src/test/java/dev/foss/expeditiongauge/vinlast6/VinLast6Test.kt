package dev.foss.expeditiongauge.vinlast6

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VinLast6Test {
    @Test
    fun parseKeepsLast6Only() {
        val raw = "49 02 01 31 46 54 5A 58 31 37 32 33 31 4E 42 31 32 33 34 35"
        val vin = VinLast6.parseVin(raw)
        assertEquals("1FTZX17231NB12345", vin)
        assertEquals("B12345", VinLast6.last6(vin))
        assertEquals("VIN …B12345", VinLast6.line("B12345"))
    }

    @Test
    fun rejectsNoise() {
        assertNull(VinLast6.parseVin("NO DATA"))
        assertNull(VinLast6.parseVin("7F 09 12"))
        assertNull(VinLast6.line(null))
        assertNull(VinLast6.line(""))
    }
}
