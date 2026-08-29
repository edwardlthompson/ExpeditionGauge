package dev.foss.expeditiongauge.dtcclear

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DtcClearTest {
    @Test
    fun refusesWhileMovingOrRecording() {
        assertFalse(DtcClear.canClear(5f, recording = false))
        assertFalse(DtcClear.canClear(0f, recording = true))
        assertTrue(DtcClear.canClear(0.1f, recording = false))
        assertTrue(DtcClear.canClear(null, recording = false))
    }

    @Test
    fun parseAckAccepts44AndRejectsNegativeResponse() {
        assertTrue(DtcClear.parseAck("44"))
        assertTrue(DtcClear.parseAck("OK\n44\n>"))
        assertFalse(DtcClear.parseAck("7F0412"))
        assertFalse(DtcClear.parseAck("7F4412"))
        assertFalse(DtcClear.parseAck(null))
        assertFalse(DtcClear.parseAck(""))
    }
}
