package dev.foss.expeditiongauge.car.aaparkedvoice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaParkedVoiceTest {
    @Test
    fun announcesOnlyWhenParked() {
        assertTrue(AaParkedVoice.canAnnounce(true))
        assertFalse(AaParkedVoice.canAnnounce(false))
        assertEquals("Recording", AaParkedVoice.phrase(recordingAfter = true))
        assertEquals("Stopped", AaParkedVoice.phrase(recordingAfter = false))
    }
}
