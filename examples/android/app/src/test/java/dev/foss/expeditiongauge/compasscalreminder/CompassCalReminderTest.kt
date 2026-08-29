package dev.foss.expeditiongauge.compasscalreminder

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CompassCalReminderTest {
    @Test
    fun remindsAfterSpike() {
        assertFalse(CompassCalReminder.shouldRemind(50f, 55f))
        assertTrue(CompassCalReminder.shouldRemind(50f, 95f))
        assertTrue(CompassCalReminder.magnitude(3f, 4f, 0f) == 5f)
    }
}
