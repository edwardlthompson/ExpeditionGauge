package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidAutoBridgeMetricsTest {
    @Test
    fun toCarMetrics_formatsMetricSpeedAndLatG() {
        val snapshot = TelemetrySnapshot(
            speedMps = 27.78f,
            latG = 0.85f,
            pitchDeg = 2.0f,
            rollDeg = -1.0f,
            driftAngleDeg = 5.0f,
            rpm = 3200f,
            throttlePct = 45f,
        )

        val metrics = AndroidAutoBridge.run { snapshot.toCarMetrics(useMetric = true) }

        assertEquals("100 KM/H", metrics["speed"])
        assertEquals("0.85 G", metrics["latG"])
        assertEquals("+5.0°", metrics["beta"])
        assertEquals("3200 rpm", metrics["rpm"])
    }

    @Test
    fun fakeBridge_recordingDelegationTracksState() {
        val bridge = FakeRecordingBridge()

        assertFalse(bridge.isRecording())
        assertTrue(bridge.startRecording())
        assertTrue(bridge.isRecording())
        assertTrue(bridge.stopRecording())
        assertFalse(bridge.isRecording())
    }

    private class FakeRecordingBridge : CarAppBridge {
        private var recording = false

        override fun isAndroidAutoEnabled(): Boolean = true

        override fun hudTiles(): CarHudTiles = CarHudTiles(
            gMeter = CarHudTile("G", "", ""),
            telemetry = CarHudTile("T", "", ""),
            tpms = CarHudTile("P", "", ""),
        )

        override fun metricValues(): Map<String, String> = emptyMap()

        override fun isRecording(): Boolean = recording

        override fun startRecording(): Boolean {
            recording = true
            return true
        }

        override fun stopRecording(): Boolean {
            recording = false
            return true
        }

        override fun markEvent(): Boolean = recording

        override fun setInvalidationListener(listener: (() -> Unit)?) = Unit
    }
}
