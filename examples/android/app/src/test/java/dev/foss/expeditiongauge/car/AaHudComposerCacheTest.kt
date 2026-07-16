package dev.foss.expeditiongauge.car

import androidx.car.app.model.CarIcon
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertSame
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AaHudComposerCacheTest {
    @Test
    fun identicalAttitude_reusesCarIconInstance() {
        val composer = AaHudComposer(RuntimeEnvironment.getApplication())
        val snap = TelemetrySnapshot(pitchDeg = 1.0f, rollDeg = -0.5f)
        val built = CarHudTiles(
            gMeter = CarHudTile("Attitude", "P +1° · R −1°", ""),
            telemetry = CarHudTile("Telemetry", "x", ""),
            tpms = CarHudTile("TPMS", "y", ""),
        )
        val spec = AaDisplaySpec.DEFAULT
        val a = composer.compose(snap, InclinometerStyle.LADDER, emptySet(), AlertThresholds(), spec, built)
        val b = composer.compose(
            snap.copy(pitchDeg = 1.05f),
            InclinometerStyle.LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
            built,
        )
        assertSame(a.gMeter.image, b.gMeter.image)

        val c = composer.compose(
            snap.copy(pitchDeg = 5f),
            InclinometerStyle.LADDER,
            emptySet(),
            AlertThresholds(),
            spec,
            built,
        )
        assertNotSame(a.gMeter.image, c.gMeter.image)
    }
}
