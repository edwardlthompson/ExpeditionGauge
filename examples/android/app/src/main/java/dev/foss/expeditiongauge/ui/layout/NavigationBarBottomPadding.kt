package dev.foss.expeditiongauge.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Bottom [navigationBars] inset for record controls, scrubbers, and FABs. */
@Composable
fun Modifier.navigationBarBottomPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
