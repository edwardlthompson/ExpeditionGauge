package dev.foss.expeditiongauge.feedback

import dev.foss.expeditiongauge.privacyreport.ReportMarkdown
import dev.foss.expeditiongauge.privacyreport.SanitizeReport

object FeedbackPreview {
    fun text(kind: String, description: String?, stack: String?): String =
        ReportMarkdown.build(kind = kind, description = description, stack = stack)

    fun canSubmit(description: String?, stack: String?): Boolean =
        SanitizeReport.text(description).isNotBlank() ||
            SanitizeReport.text(stack, stack = true).isNotBlank()
}
