package dev.foss.expeditiongauge.gauge

enum class AttitudeGaugeMode {
    ATTITUDE,
    G_FORCE,
    HYBRID,
    INCLINOMETER,
}

/** Toggle between inclinometer and ball G-meter (any non-inclinometer → inclinometer). */
fun AttitudeGaugeMode.toggleGMeterInclinometer(): AttitudeGaugeMode =
    if (this == AttitudeGaugeMode.INCLINOMETER) {
        AttitudeGaugeMode.ATTITUDE
    } else {
        AttitudeGaugeMode.INCLINOMETER
    }
