package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

internal class RecordingCrawlSmoother {
    private var profile: CrawlingModeProfile? = null
    private val speedSamples = ArrayDeque<Pair<Long, Float>>()

    fun reset(profile: CrawlingModeProfile?) {
        this.profile = profile
        speedSamples.clear()
    }

    fun smooth(snapshot: TelemetrySnapshot): TelemetrySnapshot {
        val active = profile ?: return snapshot
        val now = snapshot.timestampMs
        speedSamples.addLast(now to snapshot.speedMps)
        val cutoff = now - active.gpsSmoothingWindowMs
        while (speedSamples.isNotEmpty() && speedSamples.first().first < cutoff) {
            speedSamples.removeFirst()
        }
        if (speedSamples.isEmpty()) return snapshot
        val avg = speedSamples.map { it.second }.average().toFloat()
        return snapshot.copy(speedMps = avg)
    }
}
