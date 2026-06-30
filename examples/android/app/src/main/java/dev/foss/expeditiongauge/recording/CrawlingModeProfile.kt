package dev.foss.expeditiongauge.recording

/**
 * Sample-rate and fusion priorities for low-speed off-road / crawl sessions.
 */
data class CrawlingModeProfile(
    val imuSampleRateHz: Int = 20,
    val gpsSmoothingWindowMs: Long = 500L,
    val emphasizeAttitude: Boolean = true,
    val deemphasizeMap: Boolean = true,
    val maxPhoneOnlyRateHz: Int = 15,
) {
    fun effectiveImuRateHz(externalImuConnected: Boolean): Int =
        if (externalImuConnected) imuSampleRateHz else minOf( imuSampleRateHz, maxPhoneOnlyRateHz)

    companion object {
        val Default = CrawlingModeProfile()

        fun forMode(mode: RecordingMode, externalImuConnected: Boolean = false): CrawlingModeProfile =
            when (mode) {
                RecordingMode.CRAWLING -> Default.copy(
                    imuSampleRateHz = Default.effectiveImuRateHz(externalImuConnected),
                )
                RecordingMode.NORMAL -> CrawlingModeProfile(
                    imuSampleRateHz = 10,
                    gpsSmoothingWindowMs = 200L,
                    emphasizeAttitude = false,
                    deemphasizeMap = false,
                    maxPhoneOnlyRateHz = 10,
                )
            }
    }
}
