package dev.foss.expeditiongauge.ui.dashboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneHudChromeTest {
    @Test
    fun includesAaNavActionsPlusMenu() {
        val ids = PhoneHudChrome.actions(recording = false, markEnabled = true)
        assertEquals(listOf("menu", "mute", "record", "screenshot", "level"), ids)
        assertTrue(ids.containsAll(listOf("mute", "record", "screenshot", "level")))
    }

    @Test
    fun addsMarkOnlyWhileRecording() {
        assertTrue(PhoneHudChrome.actions(recording = true, markEnabled = true).contains("mark"))
        assertEquals(
            listOf("menu", "mute", "record", "screenshot", "level"),
            PhoneHudChrome.actions(recording = true, markEnabled = false),
        )
    }
}
