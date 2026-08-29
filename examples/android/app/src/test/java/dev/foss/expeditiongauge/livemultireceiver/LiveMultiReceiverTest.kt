package dev.foss.expeditiongauge.livemultireceiver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveMultiReceiverTest {
    @Test
    fun capsFanoutAtEight() {
        assertTrue(LiveMultiReceiver.accept(3))
        assertFalse(LiveMultiReceiver.accept(9))
        assertEquals(8, LiveMultiReceiver.fanout("x", 20).size)
        assertEquals("2 / 8", LiveMultiReceiver.label(2))
    }
}
