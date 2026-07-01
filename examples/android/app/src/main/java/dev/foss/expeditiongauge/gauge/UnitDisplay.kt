package dev.foss.expeditiongauge.gauge

import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit

/** Store SI at boundaries; format for display using user unit prefs. */
object UnitDisplay {
    fun speedMpsToDisplay(mps: Float, unit: SpeedUnit): Float =
        if (unit == SpeedUnit.METRIC) mps * 3.6f else mps * 2.23694f

    fun speedUnitLabel(unit: SpeedUnit): String =
        if (unit == SpeedUnit.METRIC) "km/h" else "mph"

    fun speedAlertLabel(unit: SpeedUnit): String =
        if (unit == SpeedUnit.METRIC) "km/h" else "mph"

    fun pressureKpaToDisplay(kpa: Float, unit: PressureUnit): Float =
        if (unit == PressureUnit.KPA) kpa else kpa / 6.894757f

    fun pressureUnitLabel(unit: PressureUnit): String =
        if (unit == PressureUnit.KPA) "kPa" else "psi"

    fun tempCToDisplay(celsius: Float, unit: TempUnit): Float =
        if (unit == TempUnit.CELSIUS) celsius else celsius * 9f / 5f + 32f

    fun tempUnitLabel(unit: TempUnit): String =
        if (unit == TempUnit.CELSIUS) "°C" else "°F"

    fun altitudeMToDisplay(meters: Double, useMetric: Boolean): Int =
        if (useMetric) meters.toInt() else (meters * 3.28084).toInt()

    fun altitudeUnitLabel(useMetric: Boolean): String = if (useMetric) "m" else "ft"
}
