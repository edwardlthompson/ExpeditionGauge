package dev.foss.expeditiongauge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ExpeditionGaugeUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun skipOnboardingIfShown() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Skip tour").performClick()
    }

    @Test
    fun opensSettingsPanelWithThemeAndUpdateControls() {
        skipOnboardingIfShown()
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check for updates").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark theme").performClick()
        composeTestRule.onNodeWithText("Close settings").performClick()
    }

    @Test
    fun opensAboutPanelWithVersion() {
        skipOnboardingIfShown()
        composeTestRule.onNodeWithContentDescription("About").performClick()
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("Installed format: apk").assertIsDisplayed()
    }
}
