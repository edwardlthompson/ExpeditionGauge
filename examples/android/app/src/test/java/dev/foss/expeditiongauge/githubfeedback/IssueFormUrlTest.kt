package dev.foss.expeditiongauge.githubfeedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IssueFormUrlTest {
    @Test
    fun placeholderRepoNeverBuildsUrl() {
        assertTrue(IssueFormUrl.isPlaceholderRepo("OWNER/REPO"))
        assertEquals("", IssueFormUrl.build("OWNER/REPO", "bug_report.yml", mapOf("title" to "x")).url)
    }

    @Test
    fun crashTitleUsesFingerprint() {
        assertEquals("[crash] a1b2c3d4e5f6 TypeError", IssueFormUrl.crashTitle("a1b2c3d4e5f6", "TypeError"))
    }

    @Test
    fun largeBodyUsesClipboardFallback() {
        val huge = "x".repeat(3000)
        val built = IssueFormUrl.build(
            "edwardlthompson/ExpeditionGauge",
            "bug_report.yml",
            mapOf("title" to "bug", "description" to huge),
        )
        assertTrue(built.bodyTooLarge)
        assertTrue(built.url.startsWith("https://"))
        assertFalse(built.url.contains(huge))
    }
}
