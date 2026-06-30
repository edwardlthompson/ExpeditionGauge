package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlin.math.abs

class LiveTelemetryEncoder(
    private val minIntervalMs: Long = 100L,
    private val speedDeltaThreshold: Float = 0.5f,
    private val betaDeltaThreshold: Float = 1f,
) {
    private var lastSentMs: Long = 0L
    private var lastSample: LiveSampleDto? = null

    fun encodeIfChanged(snapshot: TelemetrySnapshot, tpmsJson: String? = null): String? {
        val now = snapshot.timestampMs
        if (now - lastSentMs < minIntervalMs) return null
        val dto = LiveSampleDto(
            timestampMs = now,
            speedMps = snapshot.speedMps,
            latG = snapshot.latG,
            betaDeg = snapshot.driftAngleDeg,
            pitchDeg = snapshot.pitchDeg,
            rollDeg = snapshot.rollDeg,
            headingDeg = snapshot.headingDeg,
            tpmsJson = tpmsJson,
        )
        val prev = lastSample
        if (prev != null && !hasSignificantChange(prev, dto)) return null
        lastSentMs = now
        lastSample = dto
        return dto.toJson()
    }

    fun reset() {
        lastSentMs = 0L
        lastSample = null
    }

    private fun hasSignificantChange(prev: LiveSampleDto, next: LiveSampleDto): Boolean {
        if (abs(next.speedMps - prev.speedMps) >= speedDeltaThreshold) return true
        val prevBeta = prev.betaDeg ?: 0f
        val nextBeta = next.betaDeg ?: 0f
        if (abs(nextBeta - prevBeta) >= betaDeltaThreshold) return true
        if (abs(next.latG - prev.latG) >= 0.1f) return true
        return false
    }
}
