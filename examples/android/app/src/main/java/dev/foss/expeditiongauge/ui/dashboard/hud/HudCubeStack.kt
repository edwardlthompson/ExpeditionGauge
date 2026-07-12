package dev.foss.expeditiongauge.ui.dashboard.hud

import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Stacks HUD tiles as identical 1:1 squares centered in the usable area.
 * [horizontalInset] reserves equal left/right gutters (e.g. for side chrome).
 */
@Composable
fun HudCubeStack(
    isPortraitLayout: Boolean,
    modifier: Modifier = Modifier,
    horizontalInset: Dp = 0.dp,
    tiles: List<@Composable () -> Unit>,
) {
    if (tiles.isEmpty()) return
    val tileBounds = remember(tiles.size) { Array(tiles.size) { Rect() } }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        val count = tiles.size.coerceAtLeast(1)
        val usableWidth = (maxWidth - horizontalInset * 2).coerceAtLeast(1.dp)
        val tileSize: Dp = if (isPortraitLayout) {
            minOf(usableWidth, maxHeight / count)
        } else {
            minOf(usableWidth / count, maxHeight)
        }
        fun publishBounds() {
            val live = tileBounds.filter { it.width() > 0 && it.height() > 0 }
            if (live.isEmpty()) return
            HudCubeScreenshotBounds.updateTiles(live.map { Rect(it) })
            HudCubeScreenshotBounds.updateUnion(
                left = live.minOf { it.left },
                top = live.minOf { it.top },
                right = live.maxOf { it.right },
                bottom = live.maxOf { it.bottom },
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isPortraitLayout) {
                Column(
                    modifier = Modifier.size(width = tileSize, height = tileSize * count),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    tiles.forEachIndexed { index, tile ->
                        CubeTileSlot(index, tileSize, tileBounds, ::publishBounds, tile)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.size(width = tileSize * count, height = tileSize),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    tiles.forEachIndexed { index, tile ->
                        CubeTileSlot(index, tileSize, tileBounds, ::publishBounds, tile)
                    }
                }
            }
        }
    }
}

@Composable
private fun CubeTileSlot(
    index: Int,
    tileSize: Dp,
    tileBounds: Array<Rect>,
    onBoundsChanged: () -> Unit,
    content: @Composable () -> Unit,
) {
    HudSquareTile(
        modifier = Modifier
            .size(tileSize)
            .onGloballyPositioned { coords ->
                val o = coords.positionInWindow()
                val edge = min(coords.size.width, coords.size.height)
                val left = o.x.roundToInt()
                val top = o.y.roundToInt()
                tileBounds[index].set(left, top, left + edge, top + edge)
                onBoundsChanged()
            },
    ) { content() }
}
