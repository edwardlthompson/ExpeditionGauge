package dev.foss.expeditiongauge

import org.junit.Assert.assertEquals
import org.junit.Test

class SensorHoldTest {
    @Test
    fun startStopOnlyOnZeroToOneAndOneToZero() {
        var starts = 0
        var stops = 0
        val hold = SensorHold(onStart = { starts++ }, onStop = { stops++ })

        hold.acquire()
        assertEquals(1, starts)
        assertEquals(0, stops)
        assertEquals(1, hold.holdCount())

        hold.acquire()
        assertEquals(1, starts)
        assertEquals(2, hold.holdCount())

        hold.release()
        assertEquals(0, stops)
        assertEquals(1, hold.holdCount())

        hold.release()
        assertEquals(1, stops)
        assertEquals(0, hold.holdCount())

        hold.release()
        assertEquals(1, stops)
        assertEquals(0, hold.holdCount())
    }
}
