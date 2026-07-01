package dev.foss.expeditiongauge.gauge

import kotlin.math.roundToInt

enum class GaugeZone {
    Safe,
    Caution,
    Critical,
}

object GaugeLogic {
    const val RING_10_DEG = 10f
    const val RING_20_DEG = 20f
    const val RING_30_DEG = 30f

    fun formatSignedDegrees(value: Float): String {
        val sign = if (value >= 0f) "+" else ""
        return "$sign${"%.1f".format(value)}°"
    }

    fun formatWholeDegrees(value: Float): String {
        val sign = if (value >= 0f) "+" else ""
        return "$sign${value.roundToInt()}°"
    }

    fun formatAltitude(meters: Double, useMetric: Boolean): String {
        return if (useMetric) {
            "${meters.roundToInt()} m"
        } else {
            "${(meters * 3.28084).roundToInt()} ft"
        }
    }

    fun formatHeading(headingDeg: Float): String {
        val normalized = ((headingDeg % 360f) + 360f) % 360f
        return "${normalized.toInt()}°"
    }

    fun formatSpeedMps(speedMps: Float, useMetric: Boolean = true): String {
        val value = if (useMetric) speedMps * 3.6f else speedMps * 2.23694f
        return value.toInt().toString()
    }

    /** Three-character speed field for compact HUD row (e.g. ` 45`, `128`). */
    fun formatSpeedPadded(speedMps: Float, useMetric: Boolean = true): String {
        val value = if (useMetric) speedMps * 3.6f else speedMps * 2.23694f
        return value.toInt().coerceAtLeast(0).coerceAtMost(999).toString().padStart(3)
    }

    fun formatHeadingPadded(headingDeg: Float): String {
        val normalized = ((headingDeg % 360f) + 360f) % 360f
        return "${normalized.toInt().coerceIn(0, 359).toString().padStart(3)}°"
    }

    fun speedUnitLabel(useMetric: Boolean = true): String = if (useMetric) "KM/H" else "MPH"

    fun formatWholeG(value: Float): String = value.roundToInt().toString()

    fun zoneForAngle(magnitudeDeg: Float, safeThreshold: Float, cautionThreshold: Float): GaugeZone = when {
        magnitudeDeg >= cautionThreshold -> GaugeZone.Critical
        magnitudeDeg >= safeThreshold -> GaugeZone.Caution
        else -> GaugeZone.Safe
    }
}
