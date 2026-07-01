package dev.foss.expeditiongauge.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun GaugeBackHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}
