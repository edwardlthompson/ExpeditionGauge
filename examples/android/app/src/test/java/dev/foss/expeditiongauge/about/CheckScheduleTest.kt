package dev.foss.expeditiongauge.about

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckScheduleTest {
    @Test
    fun offIntervalNeverChecks() {
        assertFalse(CheckSchedule.shouldCheck("off", null, 1_000L))
    }

    @Test
    fun dailyWaitsAFullDay() {
        val now = ProductUpdate.MS_DAY
        assertTrue(CheckSchedule.shouldCheck("daily", null, now))
        assertFalse(CheckSchedule.shouldCheck("daily", 0L, ProductUpdate.MS_DAY - 1))
        assertTrue(CheckSchedule.shouldCheck("daily", 0L, ProductUpdate.MS_DAY))
    }

    @Test
    fun weeklyRequiresSevenDays() {
        val now = 10_000_000L
        assertTrue(CheckSchedule.shouldCheck("weekly", now - 8 * 86_400_000L, now))
        assertFalse(CheckSchedule.shouldCheck("weekly", now - 86_400_000L, now))
    }

    @Test
    fun onSessionChecksOnce() {
        assertTrue(CheckSchedule.shouldCheck("on_session", null, 1L))
        assertFalse(CheckSchedule.shouldCheck("on_session", 1L, 2L))
    }
}
