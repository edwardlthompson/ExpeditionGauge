package dev.foss.expeditiongauge.boostpids

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoostPidsTest {
    @Test
    fun parseMapAfrAndBoost() {
        assertEquals(100f, BoostPids.parseMapKpa("41 0B 64")!!, 0.01f)
        assertEquals(101f, BoostPids.parseBaroKpa("41 33 65")!!, 0.01f)
        val afr = BoostPids.parseAfr("41 34 80 00")!!
        assertEquals(14.7f, afr, 0.05f)
        assertEquals(20f, BoostPids.boostKpa(120f, 100f)!!, 0.01f)
        assertNull(BoostPids.boostKpa(100f, 101f))
    }

    @Test
    fun lineJoinsPresentFields() {
        val line = BoostPids.line(BoostPidSnapshot(mapKpa = 120f, afr = 14.7f, boostKpa = 19f))
        assertTrue(line!!.contains("MAP 120 kPa"))
        assertTrue(line.contains("AFR 14.7"))
        assertTrue(line.contains("Boost 19 kPa"))
        assertNull(BoostPids.line(BoostPidSnapshot()))
        assertNull(BoostPids.parseMapKpa("NO DATA"))
    }
}
