package dev.foss.expeditiongauge.sensors

import androidx.test.core.app.ApplicationProvider
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.fusion.SensorFusionEngine
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class PhoneImuTelemetryPublisherTest {
    @Test
    fun publish_preservesObdConnectedFromBus() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bus = TelemetryBus()
        bus.publish(
            TelemetrySnapshot.empty().copy(
                obdConnected = true,
                rpm = 2_400f,
                gpsFix = true,
                speedMps = 12f,
            ),
        )
        val publisher = PhoneImuTelemetryPublisher(
            fusionEngine = SensorFusionEngine(CalibrationStore(context)),
            driftEstimator = DriftAngleEstimator(),
            telemetryBus = bus,
            bleImuManager = null,
        )
        publisher.publish()
        val out = bus.snapshots.value
        assertTrue(out.obdConnected)
        assertEquals(2_400f, out.rpm)
        assertTrue(out.gpsFix)
    }
}
