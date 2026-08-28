package dev.foss.expeditiongauge.gps

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GpsHeadingMergeTest {
    @Test
    fun unknownCourseHoldsPreviousAndImuDisplay() {
        val current = TelemetrySnapshot(
            headingDeg = 40f,
            bodyYawDeg = 40f,
            velocityHeadingDeg = 185f,
            speedMps = 12f,
        )
        val merged = GpsHeadingMerge.withCourse(
            current = current,
            speedMps = 12f,
            gpsCourseDeg = null,
        ) { this }
        assertEquals(185f, merged.headingDeg, 0.01f)
        assertEquals(185f, merged.velocityHeadingDeg!!, 0.01f)
    }

    @Test
    fun nullCourseWithNoHistoryUsesBodyYaw() {
        val current = TelemetrySnapshot(
            headingDeg = 12f,
            bodyYawDeg = 12f,
            velocityHeadingDeg = null,
        )
        val merged = GpsHeadingMerge.withCourse(
            current = current,
            speedMps = 8f,
            gpsCourseDeg = null,
        ) { this }
        assertEquals(12f, merged.headingDeg, 0.01f)
        assertNull(merged.velocityHeadingDeg)
    }

    @Test
    fun trueNorthChipCourseIsNotDropped() {
        val current = TelemetrySnapshot(bodyYawDeg = 90f, headingDeg = 90f)
        val merged = GpsHeadingMerge.withCourse(
            current = current,
            speedMps = 20f,
            gpsCourseDeg = 0f,
        ) { this }
        assertEquals(0f, merged.headingDeg, 0.01f)
        assertEquals(0f, merged.velocityHeadingDeg!!, 0.01f)
    }
}
