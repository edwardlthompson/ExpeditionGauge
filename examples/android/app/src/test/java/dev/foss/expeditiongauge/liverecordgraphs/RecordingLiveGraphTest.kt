package dev.foss.expeditiongauge.liverecordgraphs

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingLiveGraphTest {
    @Test
    fun evictsPointsOlderThanWindow() {
        val buf = ArrayDeque<LiveGraphPoint>()
        RecordingLiveGraph.retain(buf, pt(1_000L, 10f), windowMs = 5_000L)
        RecordingLiveGraph.retain(buf, pt(3_000L, 12f), windowMs = 5_000L)
        RecordingLiveGraph.retain(buf, pt(7_000L, 14f), windowMs = 5_000L)
        assertEquals(2, buf.size)
        assertEquals(3_000L, buf.first().timestampMs)
        assertEquals(14f, buf.last().speedMps)
    }

    @Test
    fun decimateKeepsEndsAndCapsCount() {
        val buf = ArrayDeque<LiveGraphPoint>()
        repeat(50) { i ->
            RecordingLiveGraph.retain(buf, pt(i * 100L, i.toFloat()), maxPoints = 50)
        }
        val out = RecordingLiveGraph.decimate(buf, maxOut = 10)
        assertTrue(out.size <= 11)
        assertEquals(buf.first().timestampMs, out.first().timestampMs)
        assertEquals(buf.last().timestampMs, out.last().timestampMs)
    }

    @Test
    fun pointFromCopiesSpeedAndLatG() {
        val snap = TelemetrySnapshot.empty().copy(timestampMs = 9L, speedMps = 8f, latG = 0.4f)
        val pt = RecordingLiveGraph.pointFrom(snap)
        assertEquals(9L, pt.timestampMs)
        assertEquals(8f, pt.speedMps)
        assertEquals(0.4f, pt.latG)
        assertEquals(listOf(8f), RecordingLiveGraph.speedSeries(listOf(pt)))
        assertEquals(listOf(0.4f), RecordingLiveGraph.latGSeries(listOf(pt)))
    }

    private fun pt(ts: Long, speed: Float) = LiveGraphPoint(ts, speed, 0.1f)
}
