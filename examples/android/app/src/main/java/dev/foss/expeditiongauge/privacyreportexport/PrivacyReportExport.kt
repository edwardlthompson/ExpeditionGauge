package dev.foss.expeditiongauge.privacyreportexport

import android.content.Intent
import dev.foss.expeditiongauge.privacyreport.ReportMarkdown

/** Share a sanitized privacy report (no raw paths or tokens). */
object PrivacyReportExport {
    const val MIME = "text/plain"

    fun markdown(
        kind: String,
        description: String?,
        stack: String? = null,
        appVersion: String? = null,
    ): String = ReportMarkdown.build(
        kind = kind,
        description = description,
        stack = stack,
        appVersion = appVersion,
    )

    fun shareIntent(body: String): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = MIME
            putExtra(Intent.EXTRA_SUBJECT, "ExpeditionGauge privacy report")
            putExtra(Intent.EXTRA_TEXT, body)
        }
}
