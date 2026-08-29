package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.map.HomeMapRegion
import dev.foss.expeditiongauge.ui.offlinegeocoder.OfflineGeocoderLabel
import dev.foss.expeditiongauge.ui.offlinetilecache.OfflineTileCacheLabel
import dev.foss.expeditiongauge.ui.osmspeedlimit.OsmSpeedLimitLabel
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun SettingsMapOptions(
    homeRegion: HomeMapRegion?,
    onUseCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.settings_maps_heading),
        modifier = modifier.fillMaxWidth(),
    )
    val summary = homeRegion?.let { region ->
        stringResource(
            R.string.settings_maps_home_summary,
            region.centerLat,
            region.centerLon,
            region.radiusKm,
        )
    } ?: stringResource(R.string.settings_maps_home_unset)
    Text(
        text = summary,
        color = GaugeScaleWhite,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_maps_home_summary"),
    )
    Button(
        onClick = onUseCurrentLocation,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_maps_use_location"),
    ) {
        Text(stringResource(R.string.settings_maps_use_location))
    }
    OfflineTileCacheLabel(cachedCount = 0)
    OsmSpeedLimitLabel(lat = homeRegion?.centerLat, lon = homeRegion?.centerLon)
    OfflineGeocoderLabel(lat = homeRegion?.centerLat, lon = homeRegion?.centerLon)
}
