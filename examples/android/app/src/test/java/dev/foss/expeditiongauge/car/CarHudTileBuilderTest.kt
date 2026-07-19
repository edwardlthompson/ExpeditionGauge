package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import org.junit.Assert.assertFalse
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
        // Secondary prioritizes speed·HDG; altitude lives on the glance bitmap.
        assertFalse(tiles.telemetry.line1.contains("Alt"))
        assertTrue(tiles.tpms.line1.contains("FL"))
        assertTrue(!tiles.tpms.line1.contains("\n"))
        // Temps stay off the truncated secondary line.
        assertFalse(tiles.tpms.line1.contains("25"))
        assertTrue(CarHudTileBuilder.secondaryText(tiles.tpms) == tiles.tpms.line1)
    }

    @Test
    fun labelsPutAltitudeAndTempsOnBitmapFields() {
        val snapshot = TelemetrySnapshot(
            speedMps = 10f,
            headingDeg = 1f,
            altitudeM = 100.0,
            frontLeftPressure = TirePressureReading(psi = 30f, tempC = 20f),
        )
        val labels = CarHudTileBuilder.labels(
            snapshot,
            SpeedUnit.IMPERIAL,
            PressureUnit.PSI,
            TempUnit.CELSIUS,
        )
        assertTrue(labels.telemetrySecondary.contains("MPH"))
        assertTrue(labels.telemetrySecondary.contains("HDG"))
        assertFalse(labels.telemetrySecondary.contains("Alt"))
        assertTrue(labels.altLabel.startsWith("Elev. "))
        assertTrue(labels.coordsLabel.contains("GPS"))
        assertTrue(labels.flBitmap.startsWith("30"))
        assertTrue(labels.flBitmap.contains("\n"))
        assertTrue(labels.flBitmap.contains("20"))
        assertFalse(labels.tpmsSecondary.contains("20"))
    }

    @Test
    fun labelsIncludeStackedLatLonWhenFixPresent() {
        val labels = CarHudTileBuilder.labels(
            TelemetrySnapshot(
                speedMps = 0f,
                headingDeg = 0f,
                latitude = 18.45725,
                longitude = -66.184583,
            ),
            SpeedUnit.METRIC,
            PressureUnit.PSI,
            TempUnit.CELSIUS,
        )
        assertTrue(labels.coordsLabel.contains("N"))
        assertTrue(labels.coordsLabel.contains("W"))
        assertTrue(labels.coordsLabel.contains("\n"))
    }
}
