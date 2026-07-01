package dev.foss.expeditiongauge.ui.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.live.LiveSampleDto
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun LiveReceiverScreen(
    latestSample: LiveSampleDto?,
    sessionId: String,
    onSessionIdChange: (String) -> Unit,
    sessionCode: String,
    onSessionCodeChange: (String) -> Unit,
    signalWss: String,
    onSignalWssChange: (String) -> Unit,
    onJoin: () -> Unit,
    onDisconnect: () -> Unit,
    isConnected: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GaugeBackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .testTag("live_receiver_screen"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.live_receiver_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        OutlinedTextField(
            value = sessionId,
            onValueChange = onSessionIdChange,
            label = { Text(stringResource(R.string.live_receiver_session_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_receiver_session_id"),
            singleLine = true,
        )
        OutlinedTextField(
            value = sessionCode,
            onValueChange = onSessionCodeChange,
            label = { Text(stringResource(R.string.live_receiver_code_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_receiver_code"),
            singleLine = true,
        )
        OutlinedTextField(
            value = signalWss,
            onValueChange = onSignalWssChange,
            label = { Text(stringResource(R.string.live_signal_url_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_receiver_signal_url"),
            singleLine = true,
        )
        if (!isConnected) {
            Button(onClick = onJoin, modifier = Modifier.testTag("live_receiver_join")) {
                Text(stringResource(R.string.live_receiver_join))
            }
        } else {
            Button(onClick = onDisconnect, modifier = Modifier.testTag("live_receiver_disconnect")) {
                Text(stringResource(R.string.live_receiver_disconnect))
            }
        }
        latestSample?.let { sample ->
            Text(
                text = stringResource(
                    R.string.live_receiver_readout,
                    sample.speedMps * 3.6f,
                    sample.latG,
                    sample.betaDeg ?: 0f,
                ),
                color = GaugeScaleWhite,
                modifier = Modifier.testTag("live_receiver_readout"),
            )
        }
        Button(onClick = onBack, modifier = Modifier.testTag("live_receiver_back")) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
