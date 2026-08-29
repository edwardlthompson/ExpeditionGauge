package dev.foss.expeditiongauge.phoneimulive

import dev.foss.expeditiongauge.live.LiveSampleDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhoneImuLiveTest {
    @Test
    fun encodesMergesAndRejectsJunk() {
        val framed = PhoneImuLive.encode(1.5f, -2f, 90f)
        val imu = PhoneImuLive.decode(framed)!!
        assertEquals(1.5f, imu.pitchDeg)
        assertNull(PhoneImuLive.decode("nope"))
        val merged = PhoneImuLive.merge(
            LiveSampleDto(1L, 0f, 0f, null, 0f, 0f, 0f),
            imu,
        )
        assertEquals(90f, merged.headingDeg)
    }
}
