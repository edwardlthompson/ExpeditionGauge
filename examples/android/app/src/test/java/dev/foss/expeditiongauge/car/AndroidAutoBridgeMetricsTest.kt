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

        val metrics = snapshot.toCarMetrics(useMetric = true)

        assertEquals("100 KM/H", metrics["speed"])
        assertEquals("0.85 G", metrics["latG"])
        assertEquals("+5°", metrics["beta"])
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

        override fun hudTiles(displaySpec: AaDisplaySpec): CarHudTiles = CarHudTiles(
            gMeter = CarHudTile("G", "", ""),
            telemetry = CarHudTile("T", "", ""),
            tpms = CarHudTile("P", "", ""),
        )

        override fun driveHud(displaySpec: AaDisplaySpec): DriveHudContent =
            DriveHudContent(
                image = androidx.car.app.model.CarIcon.APP_ICON,
                rows = listOf(DriveHudRow("T", "—")),
            )

        override fun driveHudBitmap(
            displaySpec: AaDisplaySpec,
            cubePxOverride: Int?,
            orientation: HudStripOrientation,
        ): android.graphics.Bitmap? = null

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

        override fun zeroAttitude(): Boolean = true

        override fun cycleAttitudeDisplay(): Boolean = true

        override fun captureAaScreenshot(): Boolean = true

        private var muted = false

        override fun isAlertsMuted(): Boolean = muted

        override fun setAlertsMuted(muted: Boolean): Boolean {
            this.muted = muted
            return true
        }

        override fun setInvalidationListener(listener: (() -> Unit)?) = Unit

        override fun setToastHandler(handler: ((String) -> Unit)?) = Unit

        override fun onCarSessionStarted() = Unit

        override fun onCarSessionStopped() = Unit
    }
}
