package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InclinometerSegmentLogicTest {
    @Test
    fun positivePitch_lightsLowerBarsOnly() {
        val frame = InclinometerSegmentLogic.frame(pitchDeg = 15f, rollDeg = 0f)
        assertTrue(frame.pitchUp.isNotEmpty())
        assertTrue(frame.pitchDown.isEmpty())
        assertEquals(0.5f, frame.leftRollFill, 0.001f)
        assertEquals(0.5f, frame.rightRollFill, 0.001f)
    }

    @Test
    fun negativePitch_lightsUpperBarsOnly() {
        val frame = InclinometerSegmentLogic.frame(pitchDeg = -18f, rollDeg = 0f)
        assertTrue(frame.pitchDown.isNotEmpty())
        assertTrue(frame.pitchUp.isEmpty())
    }

    @Test
    fun negativeRoll_fillsLeftDrainsRight() {
        val frame = InclinometerSegmentLogic.frame(pitchDeg = 0f, rollDeg = -20f)
        assertTrue(frame.leftRollFill > frame.rightRollFill)
        assertTrue(frame.pitchUp.isEmpty())
        assertTrue(frame.pitchDown.isEmpty())
    }

    @Test
    fun levelRoll_bothSidesHalfFull() {
        val (left, right) = InclinometerSegmentLogic.rollFills(0f)
        assertEquals(0.5f, left, 0.001f)
        assertEquals(0.5f, right, 0.001f)
    }

    @Test
    fun fullScale_at45Degrees_fivePitchBarsPerSide() {
        val frame = InclinometerSegmentLogic.frame(pitchDeg = 45f, rollDeg = 0f)
        assertEquals(InclinometerSegmentLogic.BARS_PER_SIDE, frame.pitchUp.size)
    }

    @Test
    fun thresholdMarkers_clampedToMax() {
        val frame = InclinometerSegmentLogic.frame(
            pitchDeg = 0f,
            rollDeg = 0f,
            maxPitchThresholdDeg = 60f,
            maxRollThresholdDeg = 50f,
        )
        assertEquals(45f, frame.pitchMarkerDeg)
        assertEquals(45f, frame.rollMarkerDeg)
    }
}
