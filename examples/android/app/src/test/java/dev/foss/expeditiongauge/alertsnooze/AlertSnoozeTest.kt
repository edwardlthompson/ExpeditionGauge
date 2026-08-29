package dev.foss.expeditiongauge.alertsnooze

import dev.foss.expeditiongauge.alerts.AlertType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertSnoozeTest {
    @Test
    fun suppressesOnlyBeforeExpiryAndRoundTrips() {
        assertTrue(AlertSnooze.suppressed(100L, 50L))
        assertFalse(AlertSnooze.suppressed(100L, 100L))
        assertFalse(AlertSnooze.suppressed(null, 50L))
        assertEquals(5_100L, AlertSnooze.untilMs(100L, 5_000L))
        val encoded = AlertSnooze.encode(mapOf(AlertType.SPEED to 9L, AlertType.RPM to 3L))
        assertEquals(mapOf(AlertType.SPEED to 9L, AlertType.RPM to 3L), AlertSnooze.decode(encoded))
        assertTrue(AlertSnooze.decode(null).isEmpty())
        assertTrue(AlertSnooze.decode("NOPE:1").isEmpty())
    }
}
