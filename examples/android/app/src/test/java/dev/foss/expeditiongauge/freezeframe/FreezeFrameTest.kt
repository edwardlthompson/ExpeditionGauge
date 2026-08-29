package dev.foss.expeditiongauge.freezeframe

import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FreezeFrameTest {
    @Test
    fun parseDtcReads4202() {
        assertEquals("P0420", FreezeFrame.parseDtc("42 02 04 20 >"))
        assertNull(FreezeFrame.parseDtc("42 02 00 00"))
        assertNull(FreezeFrame.parseDtc("NO DATA"))
        assertNull(FreezeFrame.parseDtc("7F 02 12"))
    }

    @Test
    fun parseMode02Pids() {
        assertEquals(1000f, FreezeFrame.parseRpm("42 0C 0F A0")!!, 0.01f)
        assertEquals(48f, FreezeFrame.parseSpeedKmh("42 0D 30")!!, 0.01f)
        assertEquals(100f, FreezeFrame.parsePct("42 11 FF", "4211")!!, 0.5f)
    }

    @Test
    fun attachMatchesCode() {
        val snap = FreezeFrameSnapshot(dtc = "P0420", rpm = 2100f, speedKmh = 48f)
        val out = FreezeFrame.attach(
            listOf(DtcEntry("P0171", "Lean"), DtcEntry("P0420", "Catalyst")),
            snap,
        )
        assertNull(out[0].freezeSummary)
        assertTrue(out[1].freezeSummary!!.contains("P0420"))
        assertTrue(out[1].freezeSummary!!.contains("2100 rpm"))
        assertTrue(out[1].freezeSummary!!.contains("48 km/h"))
    }

    @Test
    fun attachEmptyOrMissingSnapUnchanged() {
        val entries = listOf(DtcEntry("P0420", "Catalyst"))
        assertEquals(
            emptyList<DtcEntry>(),
            FreezeFrame.attach(emptyList(), FreezeFrameSnapshot(dtc = "P0420")),
        )
        assertEquals(entries, FreezeFrame.attach(entries, null))
    }
}
