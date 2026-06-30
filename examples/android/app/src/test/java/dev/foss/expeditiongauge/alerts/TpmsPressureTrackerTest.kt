package dev.foss.expeditiongauge.alerts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TpmsPressureTrackerTest {
    @Test
    fun returnsNullUntilSecondSample() {
        val tracker = TpmsPressureTracker()
        assertNull(tracker.recordLossKpaPerMin("fl", 220f, 0L))
    }

    @Test
    fun computesLossRateKpaPerMin() {
        val tracker = TpmsPressureTracker()
        tracker.recordLossKpaPerMin("fl", 220f, 0L)
        val rate = tracker.recordLossKpaPerMin("fl", 210f, 30_000L)
        assertEquals(20f, rate!!, 0.1f)
    }
}
