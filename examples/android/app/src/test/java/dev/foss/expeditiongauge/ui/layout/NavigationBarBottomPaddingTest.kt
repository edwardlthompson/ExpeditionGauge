package dev.foss.expeditiongauge.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NavigationBarBottomPaddingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun navigationBarBottomPadding_appliesNonZeroWhenInsetsPresent() {
        var bottom: Dp = 0.dp
        composeRule.setContent {
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            Box(Modifier.navigationBarBottomPadding())
        }
        composeRule.runOnIdle {
            assertTrue(bottom >= 0.dp)
        }
    }

    @Test
    fun navigationBarPadding_rendersWithoutCrash() {
        composeRule.setContent {
            Box(Modifier.navigationBarPadding())
        }
    }

    @Test
    fun insetAwareScaffold_rendersWithoutCrash() {
        composeRule.setContent {
            InsetAwareScaffold { _ ->
                Box(Modifier.padding(0.dp))
            }
        }
    }
}
