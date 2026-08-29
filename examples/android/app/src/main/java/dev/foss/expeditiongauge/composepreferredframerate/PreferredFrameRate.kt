package dev.foss.expeditiongauge.composepreferredframerate

/** Vote a high refresh rate for scrolling surfaces (API 35+). */
object PreferredFrameRate {
    const val HIGH = 120f
    const val STANDARD = 60f
    const val MIN_SDK = 35

    fun vote(sdk: Int): Float = if (sdk >= MIN_SDK) HIGH else STANDARD

    fun isHighVote(sdk: Int): Boolean = sdk >= MIN_SDK
}
