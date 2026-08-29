package dev.foss.expeditiongauge.recordingpreroll

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingPrerollTest {
    @Test
    fun keepsOnlyTheWindowThenDrains() {
        val buf = ArrayDeque<TelemetrySnapshot>()
        RecordingPreroll.retain(buf, snap(1_000L), nowMs = 1_000L)
        RecordingPreroll.retain(buf, snap(3_000L), nowMs = 3_000L)
        RecordingPreroll.retain(buf, snap(7_000L), nowMs = 7_000L)
        assertEquals(2, buf.size)
        assertEquals(3_000L, buf.first().timestampMs)
        val flushed = RecordingPreroll.drain(buf)
        assertEquals(2, flushed.size)
        assertTrue(buf.isEmpty())
    }

    private fun snap(ts: Long) = TelemetrySnapshot.empty().copy(timestampMs = ts)
}
