package dev.foss.expeditiongauge.ui.offlinetilecache

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.offlinetilecache.OfflineTileCache
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun OfflineTileCacheLabel(cachedCount: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(
            R.string.offline_tile_cache_usage,
            OfflineTileCache.usageLabel(cachedCount),
        ),
        color = GaugeScaleWhite,
        modifier = modifier.testTag("offline_tile_cache_usage"),
    )
}
