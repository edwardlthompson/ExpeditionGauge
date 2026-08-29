package dev.foss.expeditiongauge.ui.corneringhistogram

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.corneringhistogram.CorneringHistogram
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun CorneringHistogramPanel(
    samples: List<SampleEntity>,
    modifier: Modifier = Modifier,
) {
    val peak = remember(samples) {
        CorneringHistogram.bins(samples).maxByOrNull { it.count }
    } ?: return
    Text(
        text = stringResource(R.string.cornering_histogram_peak, peak.startG, peak.endG, peak.count),
        color = GaugeScaleWhite,
        modifier = modifier.testTag("cornering_histogram"),
    )
}
