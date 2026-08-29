package dev.foss.expeditiongauge.ui.osmspeedlimit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.osmspeedlimit.OsmSpeedLimit
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun OsmSpeedLimitLabel(lat: Double?, lon: Double?, modifier: Modifier = Modifier) {
    val zones = OsmSpeedLimit.parse("47.32,-122.15,8.0,70")
    val kph = if (lat != null && lon != null) OsmSpeedLimit.lookup(lat, lon, zones) else null
    val extra = OsmSpeedLimit.overlayLabel(kph)
    Text(
        text = listOf(stringResource(R.string.osm_speed_limit_overlay), extra)
            .filter { it.isNotBlank() }
            .joinToString(" · "),
        color = GaugeScaleWhite,
        modifier = modifier.testTag("osm_speed_limit_overlay"),
    )
}
