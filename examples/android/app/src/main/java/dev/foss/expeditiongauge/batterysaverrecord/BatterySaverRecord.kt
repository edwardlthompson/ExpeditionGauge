package dev.foss.expeditiongauge.batterysaverrecord

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** GPS-only, 5 Hz recording profile for low battery. */
object BatterySaverRecord {
    const val INTERVAL_MS = 200L

    @Volatile
    var active: Boolean = false

    fun applyInterval(enabled: Boolean, setIntervalMs: (Long) -> Unit) {
        if (enabled) setIntervalMs(INTERVAL_MS)
    }

    fun apply(snap: TelemetrySnapshot): TelemetrySnapshot {
        if (!active) return snap
        return snap.copy(
            pitchDeg = 0f,
            rollDeg = 0f,
            latG = 0f,
            lonG = 0f,
            driftAngleDeg = null,
            bodyYawDeg = null,
            chassisTwistDeg = null,
            imuStatuses = emptyList(),
            fusionSource = "gps",
        )
    }
}
