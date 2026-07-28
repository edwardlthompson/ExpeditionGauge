package dev.foss.expeditiongauge.telemetry

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SensorLinkStateTest {
    @Test
    fun from_mapsGpsObdImuAndTpms() {
        val snapshot = TelemetrySnapshot(
            gpsFix = true,
            gpsSource = "external",
            obdConnected = true,
            frontLeftPressure = TirePressureReading(psi = 32f, stale = false),
            imuStatuses = listOf(
                ImuStatusEntry(
                    deviceId = "aa:bb",
                    label = "IMU",
                    placement = "cabin",
                    connected = true,
                    signalQuality = "Good",
                ),
            ),
        )
        val links = SensorLinkState.from(snapshot)
        assertTrue(links.gpsLinked)
        assertTrue(links.obdLinked)
        assertTrue(links.tpmsLinked)
        assertTrue(links.imuLinked)
        assertTrue(links.gpsSource == "external")
    }

    @Test
    fun from_staleTpmsIsNotLinked() {
        val snapshot = TelemetrySnapshot(
            frontLeftPressure = TirePressureReading(psi = 32f, stale = true),
        )
        assertFalse(SensorLinkState.from(snapshot).tpmsLinked)
    }

    @Test
    fun from_allDisconnectedByDefault() {
        val links = SensorLinkState.from(TelemetrySnapshot.empty())
        assertFalse(links.gpsLinked)
        assertFalse(links.obdLinked)
        assertFalse(links.tpmsLinked)
        assertFalse(links.imuLinked)
    }
}
