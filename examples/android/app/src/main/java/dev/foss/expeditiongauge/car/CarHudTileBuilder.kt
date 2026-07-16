package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TirePressureReading

/**
 * Builds 3-tile car HUD from a telemetry snapshot + unit prefs.
 * GridItem secondary text is a **single** truncated line — no newlines.
 */
object CarHudTileBuilder {
    fun build(
        snapshot: TelemetrySnapshot,
        speedUnit: SpeedUnit,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): CarHudTiles {
        val useMetric = speedUnit == SpeedUnit.METRIC
        val speedText = "${GaugeLogic.formatSpeedMps(snapshot.speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
        val hdg = snapshot.headingDeg.toInt().mod(360)
        val alt = snapshot.altitudeM?.let {
            "${UnitDisplay.altitudeMToDisplay(it, useMetric)} ${UnitDisplay.altitudeUnitLabel(useMetric)}"
        } ?: "—"

        val attitude = "P ${GaugeLogic.formatSignedDegrees(snapshot.pitchDeg)} · R ${GaugeLogic.formatSignedDegrees(snapshot.rollDeg)}"
        val telemetry = "$speedText · HDG %03d° · Alt $alt".format(hdg)
        val tpms = listOf(
            cornerLabel("FL", snapshot.frontLeftPressure, pressureUnit, tempUnit),
            cornerLabel("FR", snapshot.frontRightPressure, pressureUnit, tempUnit),
            cornerLabel("RL", snapshot.rearLeftPressure, pressureUnit, tempUnit),
            cornerLabel("RR", snapshot.rearRightPressure, pressureUnit, tempUnit),
        ).joinToString(" · ")

        return CarHudTiles(
            gMeter = CarHudTile(title = "Attitude", line1 = attitude, line2 = ""),
            telemetry = CarHudTile(title = "Telemetry", line1 = telemetry, line2 = ""),
            tpms = CarHudTile(title = "TPMS", line1 = tpms, line2 = ""),
        )
    }

    /** Flatten tile lines to one GridItem secondary string (no newlines). */
    fun secondaryText(tile: CarHudTile): String = tile.secondaryText()

    private fun cornerLabel(
        corner: String,
        reading: TirePressureReading,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): String {
        val psi = reading.psi
        if (psi == null || reading.stale) return "$corner --"
        val kpa = psi * 6.894757f
        val pressure = UnitDisplay.pressureKpaToDisplay(kpa, pressureUnit)
        val pLabel = UnitDisplay.pressureUnitLabel(pressureUnit)
        val temp = reading.tempC?.let { t ->
            "${UnitDisplay.tempCToDisplay(t, tempUnit).toInt()}${UnitDisplay.tempUnitLabel(tempUnit)}"
        } ?: "—"
        return "$corner %.0f$pLabel $temp".format(pressure)
    }
}
