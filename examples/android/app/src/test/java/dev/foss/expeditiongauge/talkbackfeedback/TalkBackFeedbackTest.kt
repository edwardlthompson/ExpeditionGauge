package dev.foss.expeditiongauge.talkbackfeedback

import org.junit.Assert.assertEquals
import org.junit.Test

class TalkBackFeedbackTest {
    @Test
    fun labelsBugAndFeature() {
        assertEquals("Report a bug", TalkBackFeedback.description("bug"))
        assertEquals("Request a feature", TalkBackFeedback.description("feature"))
        assertEquals("Feedback", TalkBackFeedback.description("other"))
    }
}
