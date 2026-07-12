package dev.foss.expeditiongauge.gauge

import dev.foss.expeditiongauge.car.gauge.InclinometerStyle

enum class AttitudeGaugeMode {
    /** Pitch ladder + communicating-vessel roll bars. */
    INCLINOMETER_LADDER,
    /** Artificial horizon line gauge. */
    INCLINOMETER_HORIZON,
    /** Twin dial gauges. */
    INCLINOMETER_DUAL_DIAL,
    /** Dual spirit-level tubes. */
    INCLINOMETER_BUBBLE,
    /** Pitch/roll ball (vehicle G-meter). */
    G_FORCE,
    COMPASS_BALL,
    ;

    companion object {
        /**
         * HUD tap/swipe order — each inclinometer style, then G-meter, then compass ball.
         */
        val DISPLAY_CYCLE: List<AttitudeGaugeMode> = listOf(
            INCLINOMETER_LADDER,
            INCLINOMETER_HORIZON,
            INCLINOMETER_DUAL_DIAL,
            INCLINOMETER_BUBBLE,
            G_FORCE,
            COMPASS_BALL,
        )
    }
}

fun AttitudeGaugeMode.isInclinometerStyle(): Boolean = when (this) {
    AttitudeGaugeMode.INCLINOMETER_LADDER,
    AttitudeGaugeMode.INCLINOMETER_HORIZON,
    AttitudeGaugeMode.INCLINOMETER_DUAL_DIAL,
    AttitudeGaugeMode.INCLINOMETER_BUBBLE,
    -> true
    else -> false
}

fun AttitudeGaugeMode.toInclinometerStyle(): InclinometerStyle? = when (this) {
    AttitudeGaugeMode.INCLINOMETER_LADDER -> InclinometerStyle.LADDER
    AttitudeGaugeMode.INCLINOMETER_HORIZON -> InclinometerStyle.HORIZON
    AttitudeGaugeMode.INCLINOMETER_DUAL_DIAL -> InclinometerStyle.DUAL_DIAL
    AttitudeGaugeMode.INCLINOMETER_BUBBLE -> InclinometerStyle.BUBBLE
    else -> null
}

fun InclinometerStyle.toAttitudeGaugeMode(): AttitudeGaugeMode = when (this) {
    InclinometerStyle.LADDER -> AttitudeGaugeMode.INCLINOMETER_LADDER
    InclinometerStyle.HORIZON -> AttitudeGaugeMode.INCLINOMETER_HORIZON
    InclinometerStyle.DUAL_DIAL -> AttitudeGaugeMode.INCLINOMETER_DUAL_DIAL
    InclinometerStyle.BUBBLE -> AttitudeGaugeMode.INCLINOMETER_BUBBLE
}

/** Advance to the next attitude display in [AttitudeGaugeMode.DISPLAY_CYCLE]. */
fun AttitudeGaugeMode.nextAttitudeDisplay(): AttitudeGaugeMode {
    val cycle = AttitudeGaugeMode.DISPLAY_CYCLE
    val index = cycle.indexOf(this)
    return if (index < 0) {
        AttitudeGaugeMode.INCLINOMETER_LADDER
    } else {
        cycle[(index + 1) % cycle.size]
    }
}

/**
 * Android Auto keeps rendering the inclinometer icon family — map compass/G-meter to ladder;
 * style-specific inclinometer modes stay as-is for AA style sync.
 */
fun AttitudeGaugeMode.forAndroidAuto(): AttitudeGaugeMode =
    when (this) {
        AttitudeGaugeMode.COMPASS_BALL,
        AttitudeGaugeMode.G_FORCE,
        -> AttitudeGaugeMode.INCLINOMETER_LADDER
        else -> this
    }
