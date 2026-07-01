package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.json.JSONObject
import kotlin.math.abs

internal class RecordingSessionPeaks {
    var peakSpeed = 0f
        private set
    var peakDrift = 0f
        private set
    var peakAbsPitch = 0f
        private set
    var peakAbsRoll = 0f
        private set

    fun reset() {
        peakSpeed = 0f
        peakDrift = 0f
        peakAbsPitch = 0f
        peakAbsRoll = 0f
    }

    fun observe(snapshot: TelemetrySnapshot) {
        peakSpeed = maxOf(peakSpeed, snapshot.speedMps)
        snapshot.driftAngleDeg?.let { peakDrift = maxOf(peakDrift, abs(it)) }
        peakAbsPitch = maxOf(peakAbsPitch, snapshot.peakAbsPitchDeg)
        peakAbsRoll = maxOf(peakAbsRoll, snapshot.peakAbsRollDeg)
    }

    fun toDeviceConfigJson(): String = JSONObject().apply {
        put("peakAbsPitchDeg", peakAbsPitch)
        put("peakAbsRollDeg", peakAbsRoll)
        put("peakSpeedMps", peakSpeed)
        put("peakDriftDeg", peakDrift)
    }.toString()
}
