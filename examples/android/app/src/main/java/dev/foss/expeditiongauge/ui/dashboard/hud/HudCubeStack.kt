package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HudCubeStack(
    isPortraitLayout: Boolean,
    modifier: Modifier = Modifier,
    tiles: List<@Composable () -> Unit>,
) {
    if (tiles.isEmpty()) return
    if (isPortraitLayout) {
        Column(modifier = modifier.fillMaxSize()) {
            tiles.forEach { tile ->
                HudSquareTile(Modifier.weight(1f).fillMaxWidth()) { tile() }
            }
        }
    } else {
        Row(modifier = modifier.fillMaxSize()) {
            tiles.forEach { tile ->
                HudSquareTile(Modifier.weight(1f).fillMaxHeight()) { tile() }
            }
        }
    }
}
