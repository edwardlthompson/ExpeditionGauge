package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import org.junit.Assert.assertTrue
import org.junit.Test

class CarHudTileBuilderTest {
    @Test
    fun buildsThreeTilesWithMetricSpeedAndTpms() {
        val snapshot = TelemetrySnapshot(
            speedMps = 27.78f,
            headingDeg = 90f,
            pitchDeg = 3f,
            rollDeg = -2f,
            latG = 0.5f,
            altitudeM = 1200.0,
            frontLeftPressure = TirePressureReading(psi = 32f, tempC = 25f),
        )
        val tiles = CarHudTileBuilder.build(
            snapshot,
            SpeedUnit.METRIC,
            PressureUnit.PSI,
            TempUnit.CELSIUS,
        )
        assertTrue(tiles.gMeter.title == "Attitude")
        assertTrue(tiles.gMeter.line1.contains("P "))
        assertTrue(tiles.gMeter.line1.contains("R "))
        assertTrue(!tiles.gMeter.line1.contains("\n"))
        assertTrue(tiles.gMeter.line2.isBlank())
        assertTrue(!tiles.gMeter.line1.contains("."))
        assertTrue(tiles.telemetry.line1.contains("KM/H"))
        assertTrue(tiles.telemetry.line1.contains("090"))
        assertTrue(!tiles.telemetry.line1.contains("\n"))
        assertTrue(tiles.tpms.line1.contains("FL"))
        assertTrue(!tiles.tpms.line1.contains("\n"))
        assertTrue(CarHudTileBuilder.secondaryText(tiles.tpms) == tiles.tpms.line1)
    }
}
