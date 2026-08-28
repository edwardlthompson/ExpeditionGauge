package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GpsCourseResolverTest {
    @Test
    fun chipCourseBeatsLatLonDelta() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = 90f,
            speedMps = 15f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.001,
            toLon = -74.0,
            previousCourseDeg = 12f,
        )
        assertEquals(90f, course!!, 0.01f)
    }

    @Test
    fun missingChipDoesNotDefaultToNorth() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = null,
            speedMps = 15f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.00001,
            toLon = -74.0,
            previousCourseDeg = null,
        )
        assertNull(course)
    }

    @Test
    fun holdsPreviousWhenSegmentTooShort() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = null,
            speedMps = 12f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.00002,
            toLon = -74.0,
            previousCourseDeg = 247f,
        )
        assertEquals(247f, course!!, 0.01f)
    }

    @Test
    fun latLonFallbackWhenChipMissingAndSegmentLong() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = null,
            speedMps = 15f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.0,
            toLon = -73.999,
            previousCourseDeg = null,
        )
        assertEquals(90f, course!!, 8f)
    }

    @Test
    fun zeroChipIsValidNorthWhenMotionAgrees() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = 0f,
            speedMps = 20f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.001,
            toLon = -74.0,
            previousCourseDeg = 2f,
        )
        assertEquals(0f, course!!, 0.01f)
    }

    @Test
    fun zeroChipRejectedWhenLatLonSaysEast() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = 0f,
            speedMps = 20f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.0,
            toLon = -73.999,
            previousCourseDeg = 80f,
        )
        assertEquals(90f, course!!, 8f)
    }

    @Test
    fun zeroChipRejectedHoldsPreviousWithoutMove() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = 0f,
            speedMps = 10f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.00001,
            toLon = -74.0,
            previousCourseDeg = 200f,
        )
        assertEquals(200f, course!!, 0.01f)
    }

    @Test
    fun networkFixesDoNotUseLatLonDelta() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = null,
            speedMps = 20f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.0,
            toLon = -73.99,
            previousCourseDeg = 33f,
            allowPositionDelta = false,
        )
        assertEquals(33f, course!!, 0.01f)
    }

    @Test
    fun poorChipAccuracyFallsBack() {
        val course = GpsCourseResolver.resolveCourseDeg(
            chipBearingDeg = 45f,
            speedMps = 15f,
            fromLat = 40.0,
            fromLon = -74.0,
            toLat = 40.001,
            toLon = -74.0,
            previousCourseDeg = 10f,
            chipBearingAccuracyDeg = 80f,
        )
        assertEquals(0f, course!!, 5f)
    }

    @Test
    fun angularDistanceWraps() {
        assertTrue(GpsCourseResolver.angularDistance(359f, 1f) < 3f)
    }
}
