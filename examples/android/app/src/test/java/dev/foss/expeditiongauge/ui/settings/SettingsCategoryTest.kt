package dev.foss.expeditiongauge.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsCategoryTest {
    @Test
    fun backFromHubExitsSettings() {
        assertEquals(SettingsBackTarget.Exit, settingsBackTarget(null))
    }

    @Test
    fun backFromCategoryReturnsToHub() {
        SettingsCategory.entries.forEach { category ->
            assertEquals(SettingsBackTarget.Hub, settingsBackTarget(category))
        }
    }
}
