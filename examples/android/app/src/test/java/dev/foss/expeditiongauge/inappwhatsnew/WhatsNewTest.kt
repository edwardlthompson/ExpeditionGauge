package dev.foss.expeditiongauge.inappwhatsnew

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsNewTest {
    @Test
    fun showsUntilCurrentVersionSeen() {
        assertTrue(WhatsNew.shouldShow(null))
        assertTrue(WhatsNew.shouldShow("2.18.12"))
        assertFalse(WhatsNew.shouldShow("2.19.0"))
        assertTrue(WhatsNew.body().contains("privacy-report"))
    }
}
