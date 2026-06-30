package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun RecordControls(
    recording: Boolean,
    onRecord: () -> Unit,
    onStop: () -> Unit,
    onSessions: () -> Unit,
    onAdvancedOptions: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        if (recording) {
            Button(
                onClick = onStop,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_stop"),
                colors = ButtonDefaults.buttonColors(containerColor = GaugeRed),
            ) {
                Text(
                    text = stringResource(R.string.recording_stop),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            TextButton(
                onClick = onAdvancedOptions,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recording_advanced_open"),
            ) {
                Text(stringResource(R.string.recording_advanced_open))
            }
        } else {
            Button(
                onClick = onRecord,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_start"),
            ) {
                Text(stringResource(R.string.recording_start))
            }
        }
        TextButton(
            onClick = onSessions,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.playback_sessions),
                color = GaugeYellow,
            )
        }
    }
}
