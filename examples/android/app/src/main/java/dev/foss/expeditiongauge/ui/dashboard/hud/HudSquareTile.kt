package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.LocalTextScale

private const val REFERENCE_TILE_DP = 180f
private const val MIN_FONT_SCALE = 0.7f
private const val MAX_FONT_SCALE = 1.85f

/** Square HUD tile; parent should pass equal width and height (e.g. Modifier.size(edge)). */
@Composable
fun HudSquareTile(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.border(1.dp, GaugeScaleWhite.copy(alpha = 0.2f)),
    ) {
        val edge = minOf(maxWidth.value, maxHeight.value)
        val scale = (edge / REFERENCE_TILE_DP).coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        CompositionLocalProvider(LocalTextScale provides scale) {
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(0.dp))) {
                content()
            }
        }
    }
}
