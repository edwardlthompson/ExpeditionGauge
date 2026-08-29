package dev.foss.expeditiongauge.ui.livemultireceiver

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.livemultireceiver.LiveMultiReceiver
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun LiveMultiReceiverLabel(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = "${stringResource(R.string.live_multi_receiver)} ${LiveMultiReceiver.label(count)}",
        color = GaugeScaleWhite,
        modifier = modifier.testTag("live_multi_receiver"),
    )
}
