package dev.foss.expeditiongauge.feedback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackPreviewTest {
    @Test
    fun previewEscapesReporterSecrets() {
        val preview = FeedbackPreview.text("bug", "<script>alert(1)</script> ghp_abcdefghijklmnopqrstuvwxyz012345", null)
        assertFalse(preview.contains("ghp_"))
        assertTrue(preview.contains("## What happened"))
    }

    @Test
    fun canSubmitNeedsDescriptionOrStack() {
        assertFalse(FeedbackPreview.canSubmit("   ", null))
        assertTrue(FeedbackPreview.canSubmit("broke", null))
        assertTrue(FeedbackPreview.canSubmit(null, "at Foo.kt:1"))
    }
}
