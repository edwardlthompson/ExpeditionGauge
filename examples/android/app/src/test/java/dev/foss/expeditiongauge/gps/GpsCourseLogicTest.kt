package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GpsCourseLogicTest {
    @Test
    fun bearing_dueNorth() {
        val bearing = GpsCourseLogic.bearingDeg(40.0, -74.0, 40.01, -74.0)
        assertEquals(0f, bearing, 1f)
    }

    @Test
    fun bearing_dueEast() {
        val bearing = GpsCourseLogic.bearingDeg(40.0, -74.0, 40.0, -73.99)
        assertEquals(90f, bearing, 2f)
    }

    @Test
    fun distance_increasesWithSeparation() {
        val near = GpsCourseLogic.distanceM(40.0, -74.0, 40.0001, -74.0)
        val far = GpsCourseLogic.distanceM(40.0, -74.0, 40.01, -74.0)
        assertTrue(far > near)
        assertTrue(far > GpsCourseLogic.MIN_SEGMENT_M)
    }

    @Test
    fun displayHeading_prefersGpsWhenMoving() {
        val hdg = GpsCourseLogic.displayHeadingDeg(
            bodyYawDeg = 10f,
            gpsCourseDeg = 95f,
            speedMps = 5f,
        )
        assertEquals(95f, hdg, 0.01f)
    }

    @Test
    fun displayHeading_keepsImuWhenStationary() {
        val hdg = GpsCourseLogic.displayHeadingDeg(
            bodyYawDeg = 10f,
            gpsCourseDeg = 95f,
            speedMps = 0.5f,
        )
        assertEquals(10f, hdg, 0.01f)
    }

    @Test
    fun reliableCourse_requiresSpeedAndDistance() {
        assertTrue(GpsCourseLogic.isReliableCourse(3f, 5f))
        assertFalse(GpsCourseLogic.isReliableCourse(1f, 5f))
        assertFalse(GpsCourseLogic.isReliableCourse(3f, 0.5f))
    }
}
