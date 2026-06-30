package dev.foss.expeditiongauge

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

class ExpeditionGaugeUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
    )

    private fun dismissStartupScreens() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithTag("onboarding_tour").fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodesWithContentDescription("Settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (composeTestRule.onAllNodesWithTag("onboarding_skip").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithTag("onboarding_skip").performClick()
            composeTestRule.waitForIdle()
        }
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithContentDescription("Settings").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun opensSettingsPanelWithThemeAndUpdateControls() {
        dismissStartupScreens()
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check for updates").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark theme").performClick()
        composeTestRule.onNodeWithText("Close settings").performClick()
    }

    @Test
    fun opensAboutPanelWithVersion() {
        dismissStartupScreens()
        composeTestRule.onNodeWithContentDescription("About").performClick()
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("Installed format: apk").assertIsDisplayed()
    }
}
