package dev.foss.expeditiongauge.wettire

import dev.foss.expeditiongauge.alerts.AlertThresholds

object WetTireAlerts {
    const val PRESSURE_FACTOR = 1.1f
    const val TEMP_OFFSET_C = -10f
    const val LOSS_FACTOR = 0.8f

    fun apply(thresholds: AlertThresholds, wet: Boolean): AlertThresholds {
        if (!wet) return thresholds
        return thresholds.copy(
            minTirePressureKpa = thresholds.minTirePressureKpa?.times(PRESSURE_FACTOR),
            maxTireTempC = thresholds.maxTireTempC?.plus(TEMP_OFFSET_C),
            rapidPressureLossKpaPerMin = thresholds.rapidPressureLossKpaPerMin?.times(LOSS_FACTOR),
        )
    }
}
