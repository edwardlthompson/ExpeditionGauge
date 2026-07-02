package dev.foss.expeditiongauge.playback

import org.junit.Assert.assertEquals
import org.junit.Test

class DrivingRouteStylingTest {
    @Test
    fun coastUsesYellowBucket() {
        assertEquals(DrivingRouteStyling.COAST_BUCKET, DrivingRouteStyling.colorBucket(0f))
    }

    @Test
    fun brakeBelowThreshold() {
        assertEquals(DrivingRouteStyling.BRAKE_BUCKET, DrivingRouteStyling.colorBucket(-0.3f))
    }

    @Test
    fun accelAboveThreshold() {
        assertEquals(DrivingRouteStyling.ACCEL_BUCKET, DrivingRouteStyling.colorBucket(0.3f))
    }

    @Test
    fun driftRouteStylingDelegatesLonAccel() {
        assertEquals(DrivingRouteStyling.BRAKE_BUCKET, DriftRouteStyling.colorBucket(12f, -0.5f))
        assertEquals(DrivingRouteStyling.ACCEL_BUCKET, DriftRouteStyling.colorBucket(-12f, 0.5f))
    }
}
