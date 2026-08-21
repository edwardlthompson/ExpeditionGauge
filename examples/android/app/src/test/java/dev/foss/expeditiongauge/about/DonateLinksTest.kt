package dev.foss.expeditiongauge.about

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DonateLinksTest {
    @Test
    fun ensureVenmoReplacesPlaceholderAndKeepsVenmoFirst() {
        val raw = DonationsConfig(
            enabled = false,
            message = "Thanks",
            links = listOf(DonationLink("[INSERT METHOD]", "https://example.com/donate")),
        )
        val cfg = DonateLinks.ensureVenmo(raw)
        assertTrue(cfg.enabled)
        assertEquals(1, cfg.links.size)
        assertEquals(DonateLinks.LABEL, cfg.links[0].label)
        assertEquals(DonateLinks.VENMO_URL, cfg.links[0].url)
        assertFalse(DonateLinks.isPlaceholderLink(cfg.links[0]))
    }
}
