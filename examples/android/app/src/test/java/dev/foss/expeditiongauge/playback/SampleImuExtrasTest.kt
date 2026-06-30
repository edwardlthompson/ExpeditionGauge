package dev.foss.expeditiongauge.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SampleImuExtrasTest {
    @Test
    fun parsesImuDevicesFromExtras() {
        val json = """
            {"imuDevices":[{"deviceId":"imu-a","placement":"front_left","latG":0.8,"filteredYawDeg":12.5}]}
        """.trimIndent()
        val corners = SampleImuExtras.corners(json)
        assertEquals(1, corners.size)
        assertEquals("imu-a", corners[0].deviceId)
        assertEquals(0.8f, corners[0].latG ?: 0f, 0.01f)
    }

    @Test
    fun multiImuDetectedWithTwoDevices() {
        val json = """
            {"imuDevices":[{"deviceId":"a","placement":"fl"},{"deviceId":"b","placement":"fr"}]}
        """.trimIndent()
        assertTrue(SampleImuExtras.hasMultiImu(json))
    }
}
