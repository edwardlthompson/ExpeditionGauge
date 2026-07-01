package dev.foss.expeditiongauge.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** All [navigationBars] insets — use for full-screen HUD (landscape puts nav on the side). */
@Composable
fun Modifier.navigationBarPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars)

/** Bottom [navigationBars] inset for record controls, scrubbers, and FABs. */
@Composable
fun Modifier.navigationBarBottomPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
