package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TirePressureReading

/** Builds 3-tile car HUD from a telemetry snapshot + unit prefs. */
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

        return CarHudTiles(
            gMeter = CarHudTile(
                title = "Attitude",
                line1 = "P ${GaugeLogic.formatSignedDegrees(snapshot.pitchDeg)}",
                line2 = "R ${GaugeLogic.formatSignedDegrees(snapshot.rollDeg)}",
            ),
            telemetry = CarHudTile(
                title = "Telemetry",
                line1 = speedText,
                line2 = "HDG %03d°".format(hdg),
                line3 = "Alt $alt",
            ),
            tpms = CarHudTile(
                title = "TPMS",
                line1 = cornerLabel("FL", snapshot.frontLeftPressure, pressureUnit, tempUnit),
                line2 = cornerLabel("FR", snapshot.frontRightPressure, pressureUnit, tempUnit),
                line3 = "${cornerLabel("RL", snapshot.rearLeftPressure, pressureUnit, tempUnit)}  " +
                    cornerLabel("RR", snapshot.rearRightPressure, pressureUnit, tempUnit),
            ),
        )
    }

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
