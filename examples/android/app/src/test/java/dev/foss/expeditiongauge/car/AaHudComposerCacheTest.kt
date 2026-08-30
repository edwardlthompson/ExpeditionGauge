package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AaHudComposerCacheTest {
    @Test
    fun identicalAttitude_reusesDriveHudCarIcon() {
        val composer = AaHudComposer(RuntimeEnvironment.getApplication())
        val snap = TelemetrySnapshot(pitchDeg = 1.0f, rollDeg = -0.5f, speedMps = 10f)
        val spec = AaDisplaySpec.DEFAULT
        val a = composer.composeDriveHud(
            snap, AttitudeGaugeMode.INCLINOMETER_LADDER, emptySet(), AlertThresholds(), spec,
        )
        val b = composer.composeDriveHud(
            snap.copy(pitchDeg = 1.05f),
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
        )
        assertSame(a.image, b.image)

        val c = composer.composeDriveHud(
            snap.copy(pitchDeg = 5f),
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
        )
        assertNotSame(a.image, c.image)
    }

    @Test
    fun satelliteCountChange_rebuildsDriveHud() {
        val composer = AaHudComposer(RuntimeEnvironment.getApplication())
        val spec = AaDisplaySpec.DEFAULT
        val a = composer.composeDriveHud(
            TelemetrySnapshot(numSatellites = 8),
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
        )
        val b = composer.composeDriveHud(
            TelemetrySnapshot(numSatellites = 14),
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
        )
        assertNotSame(a.image, b.image)
    }
}
