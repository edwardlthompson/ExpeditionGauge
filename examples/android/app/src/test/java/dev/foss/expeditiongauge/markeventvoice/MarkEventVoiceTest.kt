package dev.foss.expeditiongauge.markeventvoice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarkEventVoiceTest {
    @Test
    fun attachesAndReadsUri() {
        val json = MarkEventVoice.withAudioUri("""{"tag":"apex"}""", "content://voice/1")
        assertEquals("content://voice/1", MarkEventVoice.audioUri(json))
        assertNull(MarkEventVoice.audioUri("""{"tag":"apex"}"""))
        val replaced = MarkEventVoice.withAudioUri(json, "file:///tmp/a.m4a")
        assertEquals("file:///tmp/a.m4a", MarkEventVoice.audioUri(replaced))
    }
}
