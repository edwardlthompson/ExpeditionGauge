package dev.foss.expeditiongauge.car.gauge

/**
 * Attitude display modes for AA Surface / Drive HUD bitmaps.
 * Mirrors phone [dev.foss.expeditiongauge.gauge.AttitudeGaugeMode] without Compose deps.
 */
enum class AaAttitudeMode {
    INCLINOMETER_LADDER,
    INCLINOMETER_HORIZON,
    INCLINOMETER_DUAL_DIAL,
    INCLINOMETER_BUBBLE,
    G_FORCE,
    COMPASS_BALL,
    ;

    fun toInclinometerStyle(): InclinometerStyle? = when (this) {
        INCLINOMETER_LADDER -> InclinometerStyle.LADDER
        INCLINOMETER_HORIZON -> InclinometerStyle.HORIZON
        INCLINOMETER_DUAL_DIAL -> InclinometerStyle.DUAL_DIAL
        INCLINOMETER_BUBBLE -> InclinometerStyle.BUBBLE
        else -> null
    }

    companion object {
        val DISPLAY_CYCLE: List<AaAttitudeMode> = entries.toList()

        fun fromInclinometerStyle(style: InclinometerStyle): AaAttitudeMode = when (style) {
            InclinometerStyle.LADDER -> INCLINOMETER_LADDER
            InclinometerStyle.HORIZON -> INCLINOMETER_HORIZON
            InclinometerStyle.DUAL_DIAL -> INCLINOMETER_DUAL_DIAL
            InclinometerStyle.BUBBLE -> INCLINOMETER_BUBBLE
        }
    }
}

fun AaAttitudeMode.next(): AaAttitudeMode {
    val cycle = AaAttitudeMode.DISPLAY_CYCLE
    val index = cycle.indexOf(this)
    return if (index < 0) AaAttitudeMode.INCLINOMETER_LADDER
    else cycle[(index + 1) % cycle.size]
}
