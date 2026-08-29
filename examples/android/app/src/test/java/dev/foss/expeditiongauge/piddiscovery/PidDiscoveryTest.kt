package dev.foss.expeditiongauge.piddiscovery

import dev.foss.expeditiongauge.settings.ObdPidConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PidDiscoveryTest {
    @Test
    fun applyEnablesKnownPidsOnly() {
        val cfg = PidDiscovery.applyToConfig(
            ObdPidConfig(),
            setOf(0x0C, 0x0D, 0x04),
        )
        assertTrue(cfg.rpm)
        assertTrue(cfg.speed)
        assertTrue(cfg.load)
        assertFalse(cfg.throttle)
        assertFalse(cfg.voltage)
        assertFalse(cfg.rearWheels)
    }

    @Test
    fun summaryFormatsHex() {
        assertEquals("04 0C 11", PidDiscovery.summary(setOf(0x11, 0x04, 0x0C)))
        assertEquals(null, PidDiscovery.summary(emptySet()))
    }
}
