package dev.foss.expeditiongauge.fordmode22

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FordMode22TempsTest {
    @Test
    fun lineJoinsPresentTemps() {
        val both = FordMode22Temps(transC = 82.4f, egtC = 410.2f)
        assertEquals("TFT 82°C · EGT 410°C", FordMode22TempLine.line(both))
        assertEquals("TFT 70°C", FordMode22TempLine.line(FordMode22Temps(transC = 70f)))
        assertNull(FordMode22TempLine.line(FordMode22Temps()))
        assertNull(FordMode22TempLine.line(null))
    }
}
