package dev.foss.expeditiongauge.privacyreport

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportMarkdownTest {
    @Test
    fun buildStripsSecrets() {
        val md = ReportMarkdown.build(
            kind = "bug",
            description = "token=ghp_abcdefghijklmnopqrstuvwxyz012345",
            stack = "at C:\\Users\\Ada\\x.kt",
        )
        assertFalse(md.contains("ghp_"))
        assertFalse(md.contains("Ada"))
        assertTrue(md.contains("## Kind"))
        assertTrue(md.contains("bug"))
    }
}
