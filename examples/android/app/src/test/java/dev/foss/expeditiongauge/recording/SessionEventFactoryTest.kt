package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SessionEventFactoryTest {
    @Test
    fun snapshotIncludesSlipThrottleRpm() {
        val entity = SessionEventFactory.fromSnapshot(
            sessionId = 1L,
            snapshot = TelemetrySnapshot(
                timestampMs = 5000L,
                speedMps = 12f,
                latG = 0.9f,
                pitchDeg = 2f,
                rollDeg = -1f,
                driftAngleDeg = 18f,
                slipRatio = 0.12f,
                throttlePct = 55f,
                rpm = 4200f,
            ),
        )
        val json = JSONObject(entity.payloadJson.orEmpty())
        assertEquals(12.0, json.getDouble("speedMps"), 0.01)
        assertEquals(0.12, json.getDouble("slipRatio"), 0.01)
        assertEquals(55.0, json.getDouble("throttlePct"), 0.01)
        assertEquals(4200.0, json.getDouble("rpm"), 0.01)
        assertTrue(json.has("betaDeg"))
    }
}
