package dev.foss.expeditiongauge.talkbackfeedback

/** TalkBack labels for About feedback actions. */
object TalkBackFeedback {
    fun description(kind: String): String = when (kind) {
        "bug" -> "Report a bug"
        "feature" -> "Request a feature"
        else -> "Feedback"
    }
}
