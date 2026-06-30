package dev.foss.expeditiongauge

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

    private fun dismissOnboardingIfShown() {
        composeTestRule.waitForIdle()
        if (composeTestRule.onAllNodesWithTag("onboarding_skip").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithTag("onboarding_skip").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun dashboardShowsRecordControlsAfterOnboarding() {
        dismissOnboardingIfShown()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Record").assertIsDisplayed()
    }

    @Test
    fun opensSettingsScreenFromDashboard() {
        dismissOnboardingIfShown()
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Close settings").performClick()
    }
}
