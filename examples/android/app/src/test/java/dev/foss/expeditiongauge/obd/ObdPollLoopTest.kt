package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdPollLoopTest {
    @Test
    fun pump_scansImmediatelyOnConnectionConfirmed() = runBlocking {
        val scans = mutableListOf<Int>()
        var loops = 0
        ObdPollLoop.pump(
            isActive = { loops < 1 },
            clock = { 10_000L },
            scheduler = ObdDtcScanScheduler(),
            currentDtcs = { emptyList() },
            onDtcs = { },
            scanDtcs = { prev ->
                scans += 1
                prev
            },
            pollOnce = { loops++ },
            delayMs = { },
        )
        assertEquals(listOf(1), scans)
    }

    @Test
    fun pump_doesNotRescanBeforeInterval() = runBlocking {
        var now = 10_000L
        var scans = 0
        var loops = 0
        ObdPollLoop.pump(
            isActive = { loops < 2 },
            clock = { now },
            scheduler = ObdDtcScanScheduler(),
            currentDtcs = { emptyList() },
            onDtcs = { },
            scanDtcs = { scans++; it },
            pollOnce = {
                loops++
                now += 1_000L
            },
            delayMs = { },
        )
        assertEquals(1, scans)
    }

    @Test
    fun pump_periodicFallbackAfterRescanInterval() = runBlocking {
        var now = 0L
        var scans = 0
        var loops = 0
        ObdPollLoop.pump(
            isActive = { loops < 2 },
            clock = { now },
            scheduler = ObdDtcScanScheduler(),
            currentDtcs = { emptyList() },
            onDtcs = { },
            scanDtcs = { scans++; it },
            pollOnce = {
                loops++
                now += ObdDtcReader.RESCAN_INTERVAL_MS
            },
            delayMs = { },
        )
        assertEquals(2, scans)
    }

    @Test
    fun pump_reconnectScansAgainBeforeRescanInterval() = runBlocking {
        var now = 10_000L
        var scans = 0
        var loops = 0
        val scheduler = ObdDtcScanScheduler()
        val scan: (List<DtcEntry>) -> List<DtcEntry> = { scans++; it }
        ObdPollLoop.pump(
            isActive = { loops < 1 },
            clock = { now },
            scheduler = scheduler,
            currentDtcs = { emptyList() },
            onDtcs = { },
            scanDtcs = scan,
            pollOnce = { loops++ },
            delayMs = { },
        )
        now = 10_400L
        loops = 0
        ObdPollLoop.pump(
            isActive = { loops < 1 },
            clock = { now },
            scheduler = scheduler,
            currentDtcs = { emptyList() },
            onDtcs = { },
            scanDtcs = scan,
            pollOnce = { loops++ },
            delayMs = { },
        )
        assertEquals(2, scans)
        assertTrue(now < 10_000L + ObdDtcReader.RESCAN_INTERVAL_MS)
    }

    @Test
    fun pump_clearAckEmptiesDtcs() = runBlocking {
        var loops = 0
        var last = listOf(DtcEntry("P0420", "Catalyst"))
        var consumed = false
        ObdPollLoop.pump(
            isActive = { loops < 1 },
            clock = { 10_000L },
            scheduler = ObdDtcScanScheduler(),
            currentDtcs = { last },
            onDtcs = { last = it },
            scanDtcs = { listOf(DtcEntry("P0420", "Catalyst")) },
            pollOnce = { loops++ },
            delayMs = { },
            consumeClear = {
                if (consumed) false else {
                    consumed = true
                    true
                }
            },
            performClear = { true },
        )
        assertEquals(emptyList<DtcEntry>(), last)
    }

    @Test
    fun pump_clearFailureLeavesDtcs() = runBlocking {
        var loops = 0
        val kept = listOf(DtcEntry("P0420", "Catalyst"))
        var last = kept
        ObdPollLoop.pump(
            isActive = { loops < 1 },
            clock = { 10_000L },
            scheduler = ObdDtcScanScheduler(),
            currentDtcs = { last },
            onDtcs = { last = it },
            scanDtcs = { last },
            pollOnce = { loops++ },
            delayMs = { },
            consumeClear = { true },
            performClear = { false },
        )
        assertEquals(kept, last)
    }
}
