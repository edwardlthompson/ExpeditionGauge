package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.gauge.CoordinateFormat
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import kotlin.math.roundToInt

/**
 * Builds 3-tile car HUD from a telemetry snapshot + unit prefs.
 * GridItem secondary text is a **single** truncated line — prioritize glance tokens
 * (speed·HDG; TPMS pressures); put lower-priority detail on tile bitmaps.
 */
object CarHudTileBuilder {
    fun build(
        snapshot: TelemetrySnapshot,
        speedUnit: SpeedUnit,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): CarHudTiles {
        val labels = labels(snapshot, speedUnit, pressureUnit, tempUnit)
        return CarHudTiles(
            gMeter = CarHudTile(title = "Attitude", line1 = labels.attitudeLine, line2 = ""),
            telemetry = CarHudTile(title = "Telemetry", line1 = labels.telemetrySecondary, line2 = ""),
            tpms = CarHudTile(title = "TPMS", line1 = labels.tpmsSecondary, line2 = ""),
        )
    }

    fun labels(
        snapshot: TelemetrySnapshot,
        speedUnit: SpeedUnit,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): CarHudLabels {
        val useMetric = speedUnit == SpeedUnit.METRIC
        val speedText = "${GaugeLogic.formatSpeedMps(snapshot.speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
        val hdg = snapshot.headingDeg.toInt().mod(360)
        val hdgText = "HDG %03d°".format(hdg)
        val alt = snapshot.altitudeM?.let {
            "${UnitDisplay.altitudeMToDisplay(it, useMetric)} ${UnitDisplay.altitudeUnitLabel(useMetric)}"
        } ?: "—"
        val altText = "Elev. $alt"
        val coordsText = CoordinateFormat.formatCompactAa(snapshot.latitude, snapshot.longitude)
        val attitude = "P ${GaugeLogic.formatSignedDegrees(snapshot.pitchDeg)} · R ${GaugeLogic.formatSignedDegrees(snapshot.rollDeg)}"
        // Secondary: highest-value tokens first (host truncates the end).
        val telemetrySecondary = "$speedText · $hdgText"
        val fl = pressureOnly("FL", snapshot.frontLeftPressure, pressureUnit)
        val fr = pressureOnly("FR", snapshot.frontRightPressure, pressureUnit)
        val rl = pressureOnly("RL", snapshot.rearLeftPressure, pressureUnit)
        val rr = pressureOnly("RR", snapshot.rearRightPressure, pressureUnit)
        val tpmsSecondary = listOf(fl, fr, rl, rr).joinToString(" · ")
        return CarHudLabels(
            attitudeLine = attitude,
            telemetrySecondary = telemetrySecondary,
            speedLabel = speedText,
            headingLabel = hdgText,
            altLabel = altText,
            coordsLabel = coordsText,
            tpmsSecondary = tpmsSecondary,
            flBitmap = bitmapCorner(snapshot.frontLeftPressure, pressureUnit, tempUnit),
            frBitmap = bitmapCorner(snapshot.frontRightPressure, pressureUnit, tempUnit),
            rlBitmap = bitmapCorner(snapshot.rearLeftPressure, pressureUnit, tempUnit),
            rrBitmap = bitmapCorner(snapshot.rearRightPressure, pressureUnit, tempUnit),
        )
    }

    /** Flatten tile lines to one GridItem secondary string (no newlines). */
    fun secondaryText(tile: CarHudTile): String = tile.secondaryText()

    private fun pressureOnly(
        corner: String,
        reading: TirePressureReading,
        pressureUnit: PressureUnit,
    ): String {
        val psi = reading.psi
        if (psi == null || reading.stale) return "$corner --"
        val kpa = psi * 6.894757f
        val pressure = UnitDisplay.pressureKpaToDisplay(kpa, pressureUnit)
        val pLabel = UnitDisplay.pressureUnitLabel(pressureUnit)
        return "$corner %.0f$pLabel".format(pressure)
    }

    private fun bitmapCorner(
        reading: TirePressureReading,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): String {
        val psi = reading.psi
        if (psi == null || reading.stale) return "--\n--"
        val kpa = psi * 6.894757f
        val pressure = UnitDisplay.pressureKpaToDisplay(kpa, pressureUnit).roundToInt().toString()
        val temp = reading.tempC?.let { t ->
            "${UnitDisplay.tempCToDisplay(t, tempUnit).roundToInt()}${UnitDisplay.tempUnitLabel(tempUnit)}"
        } ?: "--"
        return "$pressure\n$temp"
    }
}

data class CarHudLabels(
    val attitudeLine: String,
    val telemetrySecondary: String,
    val speedLabel: String,
    val headingLabel: String,
    val altLabel: String,
    val coordsLabel: String,
    val tpmsSecondary: String,
    val flBitmap: String,
    val frBitmap: String,
    val rlBitmap: String,
    val rrBitmap: String,
)
