package dev.foss.expeditiongauge.alerts

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TpmsCornerReading
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot
import kotlin.math.abs

class AlertEngine(
    private var thresholds: AlertThresholds = AlertThresholds(),
) {
    private val lastFiredMs = mutableMapOf<AlertType, Long>()

    fun updateThresholds(new: AlertThresholds) {
        thresholds = new
    }

    fun evaluate(snapshot: TelemetrySnapshot, nowMs: Long = snapshot.timestampMs): List<AlertEvent> {
        if (!thresholds.anyEnabled()) return emptyList()
        val events = mutableListOf<AlertEvent>()

        thresholds.maxLatG?.let { limit ->
            if (abs(snapshot.latG) >= limit) {
                tryFire(AlertType.LAT_G, abs(snapshot.latG), limit, nowMs)?.let(events::add)
            }
        }
        thresholds.maxAbsDriftAngleDeg?.let { limit ->
            snapshot.driftAngleDeg?.let { beta ->
                if (abs(beta) >= limit) {
                    tryFire(AlertType.DRIFT_ANGLE, abs(beta), limit, nowMs)?.let(events::add)
                }
            }
        }
        thresholds.maxSlipRatio?.let { limit ->
            snapshot.slipRatio?.let { slip ->
                if (slip >= limit) {
                    tryFire(AlertType.SLIP_RATIO, slip, limit, nowMs)?.let(events::add)
                }
            }
        }
        thresholds.maxPitchDeg?.let { limit ->
            if (abs(snapshot.pitchDeg) >= limit) {
                tryFire(AlertType.PITCH, abs(snapshot.pitchDeg), limit, nowMs)?.let(events::add)
            }
        }
        thresholds.maxRollDeg?.let { limit ->
            if (abs(snapshot.rollDeg) >= limit) {
                tryFire(AlertType.ROLL, abs(snapshot.rollDeg), limit, nowMs)?.let(events::add)
            }
        }
        thresholds.maxSpeedMps?.let { limit ->
            if (snapshot.speedMps >= limit) {
                tryFire(AlertType.SPEED, snapshot.speedMps, limit, nowMs)?.let(events::add)
            }
        }
        return events
    }

    fun evaluateObd(
        rpm: Float?,
        slipRatio: Float?,
        fuelRateLph: Float?,
        speedMps: Float,
        nowMs: Long,
    ): List<AlertEvent> {
        if (!thresholds.masterEnabled) return emptyList()
        val events = mutableListOf<AlertEvent>()
        thresholds.maxRpm?.let { limit ->
            rpm?.let { if (it >= limit) tryFire(AlertType.RPM, it, limit, nowMs)?.let(events::add) }
        }
        thresholds.minFuelEconomyKmpl?.let { limit ->
            fuelRateLph?.let { rate ->
                if (rate > 0f && speedMps > 1f) {
                    val kmpl = (speedMps * 3.6f) / rate
                    if (kmpl < limit) {
                        tryFire(AlertType.FUEL_ECONOMY, kmpl, limit, nowMs)?.let(events::add)
                    }
                }
            }
        }
        return events
    }

    fun evaluateTpms(
        tpms: TpmsSnapshot,
        nowMs: Long,
        tracker: TpmsPressureTracker,
    ): List<AlertEvent> {
        if (!thresholds.masterEnabled) return emptyList()
        val events = mutableListOf<AlertEvent>()
        val corners = listOf(
            "fl" to tpms.frontLeft,
            "fr" to tpms.frontRight,
            "rl" to tpms.rearLeft,
            "rr" to tpms.rearRight,
        )
        corners.forEach { (wheelId, reading) ->
            evaluateCorner(reading, wheelId, nowMs, tracker, events)
        }
        return events
    }

    private fun evaluateCorner(
        reading: TpmsCornerReading,
        wheelId: String,
        nowMs: Long,
        tracker: TpmsPressureTracker,
        events: MutableList<AlertEvent>,
    ) {
        thresholds.minTirePressureKpa?.let { limit ->
            reading.pressureKpa?.let { kpa ->
                if (kpa <= limit) {
                    tryFire(AlertType.TIRE_PRESSURE, kpa, limit, nowMs)?.let(events::add)
                }
            }
        }
        thresholds.maxTireTempC?.let { limit ->
            reading.tempC?.let { temp ->
                if (temp >= limit) {
                    tryFire(AlertType.TIRE_TEMP, temp, limit, nowMs)?.let(events::add)
                }
            }
        }
        thresholds.rapidPressureLossKpaPerMin?.let { limit ->
            reading.pressureKpa?.let { kpa ->
                val lossRate = tracker.recordLossKpaPerMin(wheelId, kpa, nowMs)
                if (lossRate != null && lossRate >= limit) {
                    tryFire(AlertType.PRESSURE_LOSS, lossRate, limit, nowMs)?.let(events::add)
                }
            }
        }
    }

    fun computeFuelEconomyKmpl(speedMps: Float, fuelRateLph: Float?): Float? {
        if (fuelRateLph == null || fuelRateLph <= 0f || speedMps <= 0f) return null
        return (speedMps * 3.6f) / fuelRateLph
    }

    private fun tryFire(type: AlertType, value: Float, threshold: Float, nowMs: Long): AlertEvent? {
        val last = lastFiredMs[type] ?: 0L
        if (last > 0L && nowMs - last < thresholds.cooldownMs) return null
        lastFiredMs[type] = nowMs
        return AlertEvent(type, value, threshold, nowMs)
    }

    fun resetCooldowns() {
        lastFiredMs.clear()
    }
}
