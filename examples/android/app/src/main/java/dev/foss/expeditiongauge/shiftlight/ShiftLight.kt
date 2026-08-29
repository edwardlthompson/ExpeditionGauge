package dev.foss.expeditiongauge.shiftlight

object ShiftLight {
    const val DEFAULT_RPM = 5_500f

    fun threshold(configuredRpm: Float?): Float = configuredRpm ?: DEFAULT_RPM

    fun active(rpm: Float?, thresholdRpm: Float = DEFAULT_RPM): Boolean =
        rpm != null && rpm >= thresholdRpm
}
