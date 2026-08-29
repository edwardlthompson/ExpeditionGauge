package dev.foss.expeditiongauge.sessionnotes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionNotesTest {
    @Test
    fun normalizeTrimsAndDropsBlank() {
        assertEquals("wet trail", SessionNotes.normalize("  wet trail  "))
        assertNull(SessionNotes.normalize("   "))
        assertNull(SessionNotes.normalize(null))
    }

    @Test
    fun matchesIgnoresCase() {
        assertTrue(SessionNotes.matches("Wet Trail", "trail"))
        assertFalse(SessionNotes.matches("Wet Trail", "drift"))
        assertTrue(SessionNotes.matches(null, "  "))
    }
}
