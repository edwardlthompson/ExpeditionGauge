package dev.foss.expeditiongauge.liverecordgraphs

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlin.math.ceil

data class LiveGraphPoint(
    val timestampMs: Long,
    val speedMps: Float,
    val latG: Float,
)

/** Ring buffer + decimation for live HUD graphs while recording. */
object RecordingLiveGraph {
    const val MAX_POINTS = 240
    const val WINDOW_MS = 30_000L
    const val DECIMATE_TO = 120

    fun pointFrom(snap: TelemetrySnapshot): LiveGraphPoint =
        LiveGraphPoint(
            timestampMs = snap.timestampMs,
            speedMps = snap.speedMps,
            latG = snap.latG,
        )

    fun retain(
        buffer: ArrayDeque<LiveGraphPoint>,
        point: LiveGraphPoint,
        nowMs: Long = point.timestampMs,
        windowMs: Long = WINDOW_MS,
        maxPoints: Int = MAX_POINTS,
    ) {
        val ts = if (point.timestampMs > 0L) point.timestampMs else nowMs
        val stored = if (point.timestampMs > 0L) point else point.copy(timestampMs = ts)
        if (buffer.isNotEmpty() && buffer.last().timestampMs == stored.timestampMs) {
            buffer.removeLast()
        }
        buffer.addLast(stored)
        val cutoff = ts - windowMs
        while (buffer.isNotEmpty() && buffer.first().timestampMs < cutoff) {
            buffer.removeFirst()
        }
        while (buffer.size > maxPoints) {
            buffer.removeFirst()
        }
    }

    fun decimate(
        buffer: Collection<LiveGraphPoint>,
        maxOut: Int = DECIMATE_TO,
    ): List<LiveGraphPoint> {
        if (buffer.isEmpty()) return emptyList()
        val list = buffer.toList()
        if (list.size <= maxOut) return list
        val step = ceil(list.size.toDouble() / maxOut).toInt().coerceAtLeast(1)
        return list.filterIndexed { i, _ -> i % step == 0 || i == list.lastIndex }
    }

    fun speedSeries(points: List<LiveGraphPoint>): List<Float> = points.map { it.speedMps }

    fun latGSeries(points: List<LiveGraphPoint>): List<Float> = points.map { it.latG }
}
