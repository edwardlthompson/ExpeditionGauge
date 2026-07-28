package dev.foss.expeditiongauge.alerts

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot
import kotlin.math.abs

class AlertEngine(
    private var thresholds: AlertThresholds = AlertThresholds(),
) {
    private val lastFeedbackMs = mutableMapOf<String, Long>()

    fun updateThresholds(new: AlertThresholds) {
        thresholds = new
    }

    /** Level-triggered: every condition currently over limit (no cooldown). */
    fun evaluateActive(snapshot: TelemetrySnapshot, nowMs: Long = snapshot.timestampMs): List<AlertEvent> {
        if (!thresholds.anyEnabled()) return emptyList()
        val events = mutableListOf<AlertEvent>()
        thresholds.maxLatG?.let { limit ->
            if (abs(snapshot.latG) >= limit) {
                events += AlertEvent(AlertType.LAT_G, abs(snapshot.latG), limit, nowMs)
            }
        }
        thresholds.maxAbsDriftAngleDeg?.let { limit ->
            snapshot.driftAngleDeg?.let { beta ->
                if (abs(beta) >= limit) {
                    events += AlertEvent(AlertType.DRIFT_ANGLE, abs(beta), limit, nowMs)
                }
            }
        }
        thresholds.maxSlipRatio?.let { limit ->
            snapshot.slipRatio?.let { slip ->
                if (slip >= limit) {
                    events += AlertEvent(AlertType.SLIP_RATIO, slip, limit, nowMs)
                }
            }
        }
        thresholds.maxPitchDeg?.let { limit ->
            if (abs(snapshot.pitchDeg) >= limit) {
                events += AlertEvent(AlertType.PITCH, abs(snapshot.pitchDeg), limit, nowMs)
            }
        }
        thresholds.maxRollDeg?.let { limit ->
            if (abs(snapshot.rollDeg) >= limit) {
                events += AlertEvent(AlertType.ROLL, abs(snapshot.rollDeg), limit, nowMs)
            }
        }
        thresholds.maxSpeedMps?.let { limit ->
            if (snapshot.speedMps >= limit) {
                events += AlertEvent(AlertType.SPEED, snapshot.speedMps, limit, nowMs)
            }
        }
        return events
    }

    fun evaluateActiveObd(
        rpm: Float?,
        fuelRateLph: Float?,
        speedMps: Float,
        nowMs: Long,
    ): List<AlertEvent> {
        if (!thresholds.masterEnabled) return emptyList()
        val events = mutableListOf<AlertEvent>()
        thresholds.maxRpm?.let { limit ->
            rpm?.let { if (it >= limit) events += AlertEvent(AlertType.RPM, it, limit, nowMs) }
        }
        thresholds.minFuelEconomyKmpl?.let { limit ->
            fuelRateLph?.let { rate ->
                if (rate > 0f && speedMps > 1f) {
                    val kmpl = (speedMps * 3.6f) / rate
                    if (kmpl < limit) {
                        events += AlertEvent(AlertType.FUEL_ECONOMY, kmpl, limit, nowMs)
                    }
                }
            }
        }
        return events
    }

    fun evaluateActiveTpms(
        tpms: TpmsSnapshot,
        nowMs: Long,
        tracker: TpmsPressureTracker,
    ): List<AlertEvent> = thresholds.evaluateActiveTpmsCorners(tpms, nowMs, tracker)

    /** Backward-compatible edge API: active conditions filtered by feedback cooldown. */
    fun evaluate(snapshot: TelemetrySnapshot, nowMs: Long = snapshot.timestampMs): List<AlertEvent> =
        filterFeedback(evaluateActive(snapshot, nowMs), nowMs)

    fun evaluateObd(
        rpm: Float?,
        slipRatio: Float?,
        fuelRateLph: Float?,
        speedMps: Float,
        nowMs: Long,
    ): List<AlertEvent> {
        @Suppress("UNUSED_PARAMETER")
        val unusedSlip = slipRatio
        return filterFeedback(evaluateActiveObd(rpm, fuelRateLph, speedMps, nowMs), nowMs)
    }

    fun evaluateTpms(
        tpms: TpmsSnapshot,
        nowMs: Long,
        tracker: TpmsPressureTracker,
    ): List<AlertEvent> = filterFeedback(evaluateActiveTpms(tpms, nowMs, tracker), nowMs)

    fun filterFeedback(active: List<AlertEvent>, nowMs: Long): List<AlertEvent> =
        active.mapNotNull { event ->
            val key = event.feedbackKey()
            val last = lastFeedbackMs[key] ?: 0L
            if (last > 0L && nowMs - last < thresholds.cooldownMs) {
                null
            } else {
                lastFeedbackMs[key] = nowMs
                event
            }
        }

    fun computeFuelEconomyKmpl(speedMps: Float, fuelRateLph: Float?): Float? {
        if (fuelRateLph == null || fuelRateLph <= 0f || speedMps <= 0f) return null
        return (speedMps * 3.6f) / fuelRateLph
    }

    fun resetCooldowns() {
        lastFeedbackMs.clear()
    }
}
