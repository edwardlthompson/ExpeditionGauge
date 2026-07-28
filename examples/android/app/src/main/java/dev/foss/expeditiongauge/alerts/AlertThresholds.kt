package dev.foss.expeditiongauge.alerts

data class AlertThresholds(
    val masterEnabled: Boolean = false,
    val maxLatG: Float? = null,
    val maxAbsDriftAngleDeg: Float? = null,
    val maxSlipRatio: Float? = null,
    val maxPitchDeg: Float? = null,
    val maxRollDeg: Float? = null,
    val maxRpm: Float? = null,
    val maxSpeedMps: Float? = null,
    val minFuelEconomyKmpl: Float? = null,
    val minTirePressureKpa: Float? = null,
    val maxTireTempC: Float? = null,
    val rapidPressureLossKpaPerMin: Float? = null,
    /** Repeat cadence while still over limit (beep/TTS). */
    val cooldownMs: Long = 1_000L,
) {
    fun anyEnabled(): Boolean = masterEnabled && listOf(
        maxLatG, maxAbsDriftAngleDeg, maxSlipRatio, maxPitchDeg, maxRollDeg,
        maxRpm, maxSpeedMps, minFuelEconomyKmpl, minTirePressureKpa,
        maxTireTempC, rapidPressureLossKpaPerMin,
    ).any { it != null }
}

enum class AlertType {
    LAT_G,
    DRIFT_ANGLE,
    SLIP_RATIO,
    PITCH,
    ROLL,
    RPM,
    SPEED,
    FUEL_ECONOMY,
    TIRE_PRESSURE,
    TIRE_TEMP,
    PRESSURE_LOSS,
}

enum class TireCornerId(val key: String) {
    FL("fl"),
    FR("fr"),
    RL("rl"),
    RR("rr"),
    ;

    companion object {
        fun fromKey(key: String?): TireCornerId? =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) }
    }
}

data class AlertEvent(
    val type: AlertType,
    val value: Float,
    val threshold: Float,
    val timestampMs: Long,
    val tireCorner: TireCornerId? = null,
)
