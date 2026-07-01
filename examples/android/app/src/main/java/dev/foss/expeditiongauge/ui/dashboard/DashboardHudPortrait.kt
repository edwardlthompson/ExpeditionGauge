package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.foss.expeditiongauge.ui.dashboard.hud.HudCubeLayout

@Composable
fun DashboardHudPortrait(
    props: DashboardHudProps,
    modifier: Modifier = Modifier,
) {
    HudCubeLayout(props = props, modifier = modifier)
}
