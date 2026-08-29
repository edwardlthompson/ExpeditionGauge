package dev.foss.expeditiongauge.privacyreport

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SanitizeReportTest {
    @Test
    fun nullBecomesEmpty() {
        assertEquals("", SanitizeReport.text(null))
    }

    @Test
    fun redactsSecretsAndHomePaths() {
        val raw = "boom ghp_abcdefghijklmnopqrstuvwxyz012345 at C:\\Users\\Ada\\secret.env " +
            "AKIAIOSFODNN7EXAMPLE eyJhbGciOiJIUzI1NiJ9.aaaa.bbbb"
        val out = SanitizeReport.text(raw)
        assertFalse(out.contains("ghp_"))
        assertFalse(out.contains("Ada"))
        assertFalse(out.contains("AKIA"))
        assertFalse(out.contains("eyJ"))
        assertTrue(out.contains("<redacted-secret>") || out.contains("<redacted-home>"))
    }

    @Test
    fun capsStackLines() {
        val lines = (1..300).joinToString("\n") { "frame $it" }
        val out = SanitizeReport.text(lines, stack = true)
        assertTrue(out.lines().size <= SanitizeReport.MAX_STACK_LINES)
    }
}
