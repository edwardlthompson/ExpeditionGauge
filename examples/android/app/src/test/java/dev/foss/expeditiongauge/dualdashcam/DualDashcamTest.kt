package dev.foss.expeditiongauge.dualdashcam

import org.junit.Assert.assertEquals
import org.junit.Test

class DualDashcamTest {
    @Test
    fun parseEncodeRoundTrip() {
        val clips = DualDashcam.parse("file:///front.mp4|0;file:///rear.mp4|-250")
        assertEquals(2, clips.size)
        assertEquals(-250L, clips[1].offsetMs)
        assertEquals("file:///front.mp4|0;file:///rear.mp4|-250", DualDashcam.encode(clips))
        assertEquals("a|10", DualDashcam.plus(null, "a", 10))
    }
}
