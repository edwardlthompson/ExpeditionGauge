package dev.foss.expeditiongauge.ui.live

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.live.LivePairingSession
import dev.foss.expeditiongauge.live.LiveQrGenerator
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun LivePairingSheet(
    session: LivePairingSession?,
    receiverCount: Int,
    onStopLive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (session == null) return
    val qrBitmap = remember(session.qrPayload) { LiveQrGenerator.bitmap(session.qrPayload, 320) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GaugeBackground)
            .padding(SpacingMd)
            .testTag("live_pairing_sheet"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.live_banner),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("live_banner_label"),
        )
        Text(
            text = stringResource(R.string.live_qr_stub),
            style = MaterialTheme.typography.bodySmall,
            color = GaugeYellow,
        )
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.live_qr_content_desc),
            modifier = Modifier
                .size(SpacingMd * 10)
                .testTag("live_qr_image"),
        )
        Text(
            text = stringResource(R.string.live_code, session.code),
            style = MaterialTheme.typography.headlineMedium,
            color = GaugeYellow,
            modifier = Modifier.testTag("live_code_label"),
        )
        Text(
            text = stringResource(R.string.live_receiver_count, receiverCount),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("live_receiver_count"),
        )
        Button(onClick = onStopLive, modifier = Modifier.testTag("live_stop_button")) {
            Text(stringResource(R.string.live_stop))
        }
    }
}
