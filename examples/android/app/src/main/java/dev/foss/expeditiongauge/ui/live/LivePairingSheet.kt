package dev.foss.expeditiongauge.ui.live

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.live.LivePairingSession
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

/**
 * QR pairing UI stub on recording screen (Sprint 19).
 * Displays session code + QR payload placeholder until CameraX / WebRTC wired.
 */
@Composable
fun LivePairingSheet(
    session: LivePairingSession?,
    receiverCount: Int,
    onStopLive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (session == null) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GaugeBackground)
            .padding(SpacingMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.live_banner),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.live_qr_stub),
            style = MaterialTheme.typography.bodySmall,
            color = GaugeYellow,
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(dev.foss.expeditiongauge.ui.theme.SpacingMd * 8)
                .background(GaugeYellow.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.live_qr_placeholder),
                style = MaterialTheme.typography.labelSmall,
                color = GaugeYellow,
            )
        }
        Text(
            text = stringResource(R.string.live_code, session.code),
            style = MaterialTheme.typography.headlineMedium,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.live_receiver_count, receiverCount),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onStopLive) {
            Text(stringResource(R.string.live_stop))
        }
    }
}
