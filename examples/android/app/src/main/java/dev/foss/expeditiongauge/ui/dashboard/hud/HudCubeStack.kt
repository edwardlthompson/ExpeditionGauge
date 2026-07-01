package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HudCubeStack(
    isPortraitLayout: Boolean,
    modifier: Modifier = Modifier,
    tiles: List<@Composable () -> Unit>,
) {
    if (tiles.isEmpty()) return
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val count = tiles.size.coerceAtLeast(1)
        val tileSize: Dp = if (isPortraitLayout) {
            minOf(maxWidth, maxHeight / count)
        } else {
            minOf(maxWidth / count, maxHeight)
        }
        if (isPortraitLayout) {
            Column(Modifier.fillMaxSize()) {
                tiles.forEach { tile ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        HudSquareTile(Modifier.size(tileSize)) { tile() }
                    }
                }
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                tiles.forEach { tile ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        HudSquareTile(Modifier.size(tileSize)) { tile() }
                    }
                }
            }
        }
    }
}
