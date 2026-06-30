package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (recording) GaugeRed.copy(alpha = 0.2f) else GaugeYellow.copy(alpha = 0.05f))
            .padding(SpacingMd),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (recording) {
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = GaugeRed),
            ) {
                Text(stringResource(R.string.recording_stop))
            }
            Text(
                text = stringResource(R.string.recording_live),
                color = GaugeRed,
                style = MaterialTheme.typography.labelLarge,
            )
        } else {
            Button(onClick = onRecord) {
                Text(stringResource(R.string.recording_start))
            }
        }
        Button(onClick = onSessions) {
            Text(stringResource(R.string.playback_sessions))
        }
    }
}
