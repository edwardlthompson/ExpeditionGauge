package dev.foss.expeditiongauge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.timing.PredictiveTimingEngine
import dev.foss.expeditiongauge.timing.PredictiveTimingState
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun LapTimerStrip(
    state: PredictiveTimingState,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    val engine = PredictiveTimingEngine()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.lap_timer_current, engine.formatLapTime(state.currentLapElapsedMs)),
            color = GaugeYellow,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f),
        )
        state.deltaMs?.let { delta ->
            Text(
                text = stringResource(R.string.lap_timer_delta, engine.formatDelta(delta)),
                color = if (delta <= 0) GaugeYellow else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
