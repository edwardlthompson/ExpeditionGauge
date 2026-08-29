package dev.foss.expeditiongauge.settingssearch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsSearchTest {
    @Test
    fun matchesPrivacyToAdvanced() {
        assertEquals(listOf("Advanced"), SettingsSearch.match("privacy"))
        assertTrue(SettingsSearch.match("obd").contains("Hardware"))
        assertEquals(6, SettingsSearch.match("").size)
    }
}
