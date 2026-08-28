package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdDtcScanSchedulerTest {
    @Test
    fun notDueUntilConnectionConfirmed() {
        val scheduler = ObdDtcScanScheduler()
        assertFalse(scheduler.due(0L))
        assertFalse(scheduler.due(ObdDtcReader.RESCAN_INTERVAL_MS))
    }

    @Test
    fun connectionConfirmed_isDueImmediately() {
        val scheduler = ObdDtcScanScheduler()
        scheduler.onConnectionConfirmed(12_345L)
        assertTrue(scheduler.due(12_345L))
        assertTrue(scheduler.due(12_346L))
    }

    @Test
    fun afterAttempt_waitsRescanInterval() {
        val scheduler = ObdDtcScanScheduler()
        scheduler.onConnectionConfirmed(1_000L)
        scheduler.markAttempt(1_000L)
        assertFalse(scheduler.due(1_000L))
        assertFalse(scheduler.due(1_000L + ObdDtcReader.RESCAN_INTERVAL_MS - 1))
        assertTrue(scheduler.due(1_000L + ObdDtcReader.RESCAN_INTERVAL_MS))
    }

    @Test
    fun reconnect_isDueImmediatelyEvenBeforeRescanInterval() {
        val scheduler = ObdDtcScanScheduler()
        scheduler.onConnectionConfirmed(1_000L)
        scheduler.markAttempt(1_000L)
        scheduler.onConnectionConfirmed(1_500L)
        assertTrue(scheduler.due(1_500L))
    }
}
