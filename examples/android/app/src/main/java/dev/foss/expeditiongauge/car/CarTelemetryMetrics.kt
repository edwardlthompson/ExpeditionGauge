package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

fun TelemetrySnapshot.toCarMetrics(useMetric: Boolean): Map<String, String> {
    val speed = "${GaugeLogic.formatSpeedMps(speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
    val beta = driftAngleDeg?.let { GaugeLogic.formatSignedDegrees(it) } ?: "—"
    val rpmText = rpm?.let { "${it.toInt()} rpm" } ?: "—"
    val throttleText = throttlePct?.let { "${it.toInt()}%" } ?: "—"
    return mapOf(
        "speed" to speed,
        "latG" to "%.2f G".format(latG),
        "pitch" to GaugeLogic.formatSignedDegrees(pitchDeg),
        "roll" to GaugeLogic.formatSignedDegrees(rollDeg),
        "beta" to beta,
        "rpm" to rpmText,
        "throttle" to throttleText,
    )
}
