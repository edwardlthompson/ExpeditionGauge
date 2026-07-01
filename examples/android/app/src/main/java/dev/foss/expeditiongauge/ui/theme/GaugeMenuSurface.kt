package dev.foss.expeditiongauge.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Dark cockpit-adjacent surface for drawer and menu-stack screens (independent of [ThemeMode]). */
@Composable
fun GaugeMenuSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = GaugeBackground,
        contentColor = GaugeScaleWhite,
        content = content,
    )
}
