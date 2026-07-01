package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.maplibre.compose.map.OrnamentOptions

@Composable
internal fun rememberPlaybackMapOrnamentOptions(): OrnamentOptions {
    val navBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return remember(navBottomPadding) {
        OrnamentOptions(
            padding = PaddingValues(bottom = navBottomPadding),
            isLogoEnabled = true,
            isAttributionEnabled = false,
            isCompassEnabled = false,
            isScaleBarEnabled = false,
        )
    }
}
