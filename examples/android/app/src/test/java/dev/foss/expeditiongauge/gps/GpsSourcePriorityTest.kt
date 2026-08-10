package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GpsSourcePriorityTest {
    @Test
    fun prefersExternalWhileConnectedEvenWithoutFix() {
        assertTrue(
            GpsSourcePriority.preferExternal(
                externalConnected = true,
                externalFix = NmeaFix(valid = false, timestampMs = 0L),
                nowMs = 10_000L,
            ),
        )
    }

    @Test
    fun prefersFreshValidExternalFixWhenDisconnected() {
        assertTrue(
            GpsSourcePriority.preferExternal(
                externalConnected = false,
                externalFix = NmeaFix(valid = true, timestampMs = 9_500L),
                nowMs = 10_000L,
                staleMs = 2_000L,
            ),
        )
    }

    @Test
    fun fallsBackToPhoneWhenExternalStaleAndDisconnected() {
        assertFalse(
            GpsSourcePriority.preferExternal(
                externalConnected = false,
                externalFix = NmeaFix(valid = true, timestampMs = 1_000L),
                nowMs = 10_000L,
                staleMs = 2_000L,
            ),
        )
    }

    @Test
    fun fallsBackToPhoneWhenNoExternalFix() {
        assertFalse(
            GpsSourcePriority.preferExternal(
                externalConnected = false,
                externalFix = NmeaFix(valid = false, timestampMs = 10_000L),
                nowMs = 10_000L,
            ),
        )
    }
}
