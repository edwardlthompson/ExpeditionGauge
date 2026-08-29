package dev.foss.expeditiongauge.lastsessionwidget

import org.junit.Assert.assertEquals
import org.junit.Test

class LastSessionLabelTest {
    @Test
    fun emptyWhenMissingName() {
        assertEquals("No session", LastSessionLabel.text(null, 1L))
        assertEquals("No session", LastSessionLabel.text("  ", 1L))
        assertEquals("Night run", LastSessionLabel.text("Night run", 1_700_000_000_000L))
    }
}
