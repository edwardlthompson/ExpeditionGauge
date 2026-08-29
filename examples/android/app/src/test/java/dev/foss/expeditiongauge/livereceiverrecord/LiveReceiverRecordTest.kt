package dev.foss.expeditiongauge.livereceiverrecord

import dev.foss.expeditiongauge.live.LiveSampleDto
import org.junit.Assert.assertEquals
import org.junit.Test

class LiveReceiverRecordTest {
    @Test
    fun buffersAndCapsSamples() {
        LiveReceiverRecord.clear()
        val dto = LiveSampleDto(1000L, 10f, 0.2f, 3f, 1f, 2f, 90f)
        val first = LiveReceiverRecord.remember(dto)
        assertEquals(10f, first.single().speedMps)
        repeat(LiveReceiverRecord.MAX_SAMPLES + 5) { index ->
            LiveReceiverRecord.remember(dto.copy(timestampMs = index.toLong()))
        }
        assertEquals(LiveReceiverRecord.MAX_SAMPLES, LiveReceiverRecord.snapshot().size)
        LiveReceiverRecord.clear()
    }
}
