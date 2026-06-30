package dev.foss.expeditiongauge.recording

import org.junit.Assert.assertEquals
import org.junit.Test

class CrawlingModeProfileTest {
    @Test
    fun crawlModeCapsPhoneOnlyRate() {
        val profile = CrawlingModeProfile.forMode(RecordingMode.CRAWLING, externalImuConnected = false)
        assertEquals(15, profile.imuSampleRateHz)
    }

    @Test
    fun normalModeUsesLowerRate() {
        val profile = CrawlingModeProfile.forMode(RecordingMode.NORMAL)
        assertEquals(10, profile.imuSampleRateHz)
        assertEquals(false, profile.emphasizeAttitude)
    }
}
