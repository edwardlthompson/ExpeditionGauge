package dev.foss.expeditiongauge.ble.tpms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TpmsSensorIdResolverTest {
    @Test
    fun matchLive_suffix() {
        val macs = listOf("AC:15:85:00:23:33", "AA:BB:CC:DD:EE:FF")
        assertEquals(
            listOf("AC:15:85:00:23:33"),
            TpmsSensorIdResolver.matchLiveMacs(macs, "002333"),
        )
    }

    @Test
    fun candidates_liveOnly_noOuiGuess() {
        assertTrue(TpmsSensorIdResolver.candidates(emptyList(), "155BB5").isEmpty())
        val macs = listOf("3B:60:00:15:5B:B5")
        val c = TpmsSensorIdResolver.candidates(macs, "155BB5")
        assertEquals(1, c.size)
        assertEquals("3B:60:00:15:5B:B5", c[0].macAddress)
        assertEquals(TpmsIdCandidate.Source.LiveAdvertisement, c[0].source)
    }
}
