package dev.foss.expeditiongauge.batterysaverrecord

import dev.foss.expeditiongauge.telemetry.ImuStatusEntry
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BatterySaverRecordTest {
    @Test
    fun applyLeavesSnapshotWhenInactive() {
        BatterySaverRecord.active = false
        val snap = TelemetrySnapshot.empty().copy(pitchDeg = 12f, latG = 0.4f)
        assertEquals(12f, BatterySaverRecord.apply(snap).pitchDeg)
    }

    @Test
    fun applyStripsImuWhenActive() {
        BatterySaverRecord.active = true
        val snap = TelemetrySnapshot.empty().copy(
            pitchDeg = 12f,
            rollDeg = 8f,
            latG = 0.4f,
            lonG = -0.2f,
            driftAngleDeg = 15f,
            imuStatuses = listOf(
                ImuStatusEntry("id", "imu", "dash", connected = true, signalQuality = "ok"),
            ),
        )
        val out = BatterySaverRecord.apply(snap)
        assertEquals(0f, out.pitchDeg)
        assertEquals(0f, out.latG)
        assertTrue(out.imuStatuses.isEmpty())
        assertEquals("gps", out.fusionSource)
        BatterySaverRecord.active = false
    }

    @Test
    fun applyIntervalSetsFiveHz() {
        var seen = 0L
        BatterySaverRecord.applyInterval(enabled = true) { seen = it }
        assertEquals(BatterySaverRecord.INTERVAL_MS, seen)
        seen = 0L
        BatterySaverRecord.applyInterval(enabled = false) { seen = it }
        assertEquals(0L, seen)
    }
}
