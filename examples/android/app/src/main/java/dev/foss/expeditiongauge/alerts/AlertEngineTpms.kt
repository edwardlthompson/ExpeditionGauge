package dev.foss.expeditiongauge.alerts

import dev.foss.expeditiongauge.telemetry.TpmsCornerReading
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot

internal fun AlertThresholds.evaluateActiveTpmsCorners(
    tpms: TpmsSnapshot,
    nowMs: Long,
    tracker: TpmsPressureTracker,
): List<AlertEvent> {
    if (!masterEnabled) return emptyList()
    val events = mutableListOf<AlertEvent>()
    val corners = listOf(
        TireCornerId.FL to tpms.frontLeft,
        TireCornerId.FR to tpms.frontRight,
        TireCornerId.RL to tpms.rearLeft,
        TireCornerId.RR to tpms.rearRight,
    )
    corners.forEach { (corner, reading) ->
        evaluateCornerActive(reading, corner, nowMs, tracker, events)
    }
    return events
}

internal fun AlertThresholds.evaluateCornerActive(
    reading: TpmsCornerReading,
    corner: TireCornerId,
    nowMs: Long,
    tracker: TpmsPressureTracker,
    events: MutableList<AlertEvent>,
) {
    minTirePressureKpa?.let { limit ->
        reading.pressureKpa?.let { kpa ->
            if (kpa <= limit) {
                events += AlertEvent(AlertType.TIRE_PRESSURE, kpa, limit, nowMs, corner)
            }
        }
    }
    maxTireTempC?.let { limit ->
        reading.tempC?.let { temp ->
            if (temp >= limit) {
                events += AlertEvent(AlertType.TIRE_TEMP, temp, limit, nowMs, corner)
            }
        }
    }
    rapidPressureLossKpaPerMin?.let { limit ->
        reading.pressureKpa?.let { kpa ->
            val lossRate = tracker.recordLossKpaPerMin(corner.key, kpa, nowMs)
            if (lossRate != null && lossRate >= limit) {
                events += AlertEvent(AlertType.PRESSURE_LOSS, lossRate, limit, nowMs, corner)
            }
        }
    }
}

internal fun AlertEvent.feedbackKey(): String =
    if (tireCorner != null) "${type.name}:${tireCorner.key}" else type.name
