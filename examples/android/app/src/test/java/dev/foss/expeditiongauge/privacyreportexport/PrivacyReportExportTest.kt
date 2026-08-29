package dev.foss.expeditiongauge.privacyreportexport

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyReportExportTest {
    @Test
    fun sanitizesSecretsBeforeShare() {
        val body = PrivacyReportExport.markdown(
            kind = "bug",
            description = "token=ghp_SECRETTOKEN123 and ada@example.com",
            stack = "C:\\Users\\Ada\\crash.log",
            appVersion = "2.18.12",
        )
        assertFalse(body.contains("ghp_SECRETTOKEN123"))
        assertFalse(body.contains("ada@example.com"))
        assertFalse(body.contains("C:\\Users\\Ada"))
        assertTrue(body.contains("<redacted-secret>"))
        assertTrue(body.contains("2.18.12"))
        assertTrue(PrivacyReportExport.MIME == "text/plain")
    }
}
