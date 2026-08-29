package dev.foss.expeditiongauge.recordingpreroll

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Keeps the last [WINDOW_MS] of live samples so Record can prepend them. */
object RecordingPreroll {
    const val WINDOW_MS = 5_000L

    fun retain(
        buffer: ArrayDeque<TelemetrySnapshot>,
        snap: TelemetrySnapshot,
        nowMs: Long,
        windowMs: Long = WINDOW_MS,
    ) {
        val ts = snap.timestampMs.takeIf { it > 0L } ?: nowMs
        buffer.addLast(if (snap.timestampMs > 0L) snap else snap.copy(timestampMs = ts))
        val cutoff = ts - windowMs
        while (buffer.isNotEmpty() && buffer.first().timestampMs < cutoff) {
            buffer.removeFirst()
        }
    }

    fun drain(buffer: ArrayDeque<TelemetrySnapshot>): List<TelemetrySnapshot> {
        val out = buffer.toList()
        buffer.clear()
        return out
    }
}
