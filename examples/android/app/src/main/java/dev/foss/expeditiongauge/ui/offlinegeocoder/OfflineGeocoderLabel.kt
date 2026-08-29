package dev.foss.expeditiongauge.ui.offlinegeocoder

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.foss.expeditiongauge.offlinegeocoder.OfflineGeocoder
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun OfflineGeocoderLabel(lat: Double?, lon: Double?, modifier: Modifier = Modifier) {
    Text(
        text = OfflineGeocoder.titleFor(lat, lon),
        color = GaugeScaleWhite,
        modifier = modifier.testTag("offline_geocoder_title"),
    )
}
