package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun HeadingReadout(
    headingDeg: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(SpacingSm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = GaugeLogic.formatHeading(headingDeg),
            color = GaugeScaleWhite,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.gauge_hdg_label),
            color = GaugeYellow,
        )
    }
}
