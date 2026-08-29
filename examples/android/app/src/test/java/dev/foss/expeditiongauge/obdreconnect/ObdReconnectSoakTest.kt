package dev.foss.expeditiongauge.obdreconnect

import dev.foss.expeditiongauge.obd.ObdDtcScanScheduler
import dev.foss.expeditiongauge.obd.ObdPollLoop
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdReconnectSoakTest {
    @Test
    fun eightReconnectsEachScanImmediately() = runBlocking {
        val scheduler = ObdDtcScanScheduler()
        var scans = 0
        var now = 10_000L
        repeat(ObdReconnectSoak.CYCLES) {
            var loops = 0
            ObdPollLoop.pump(
                isActive = { loops < 1 },
                clock = { now },
                scheduler = scheduler,
                currentDtcs = { emptyList() },
                onDtcs = { },
                scanDtcs = { scans++; it },
                pollOnce = { loops++ },
                delayMs = { },
            )
            now += 400L
        }
        assertEquals(ObdReconnectSoak.CYCLES, scans)
        assertTrue(ObdReconnectSoak.passed(scans))
        assertEquals("Soak 8/8", ObdReconnectSoak.line(scans))
        assertFalse(ObdReconnectSoak.passed(7))
    }
}
