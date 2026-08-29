package dev.foss.expeditiongauge.livereceiverrecord

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.live.LiveSampleDto

/** Buffer incoming live samples for a local Relive session. */
object LiveReceiverRecord {
    const val MAX_SAMPLES = 6_000

    @Volatile
    var sessionId: Long = 0L

    private val buffer = ArrayDeque<SampleEntity>()

    fun toSample(dto: LiveSampleDto, sessionId: Long = this.sessionId): SampleEntity =
        SampleEntity(
            sessionId = sessionId,
            timestampMs = dto.timestampMs,
            speedMps = dto.speedMps,
            latG = dto.latG,
            driftAngleDeg = dto.betaDeg,
            pitchDeg = dto.pitchDeg,
            rollDeg = dto.rollDeg,
            headingDeg = dto.headingDeg,
        )

    @Synchronized
    fun remember(dto: LiveSampleDto): List<SampleEntity> {
        buffer.addLast(toSample(dto))
        while (buffer.size > MAX_SAMPLES) buffer.removeFirst()
        return buffer.toList()
    }

    @Synchronized
    fun snapshot(): List<SampleEntity> = buffer.toList()

    @Synchronized
    fun clear() {
        buffer.clear()
    }
}
