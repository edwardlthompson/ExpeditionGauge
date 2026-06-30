package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.onKeyEvent
import dev.foss.expeditiongauge.playback.PlaybackEngine
import dev.foss.expeditiongauge.playback.PlaybackInputHandler
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.delay

@Composable
fun PlaybackScreen(
    engine: PlaybackEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by engine.state.collectAsStateWithLifecycle()
    val sample = state.current

    LaunchedEffect(state.playing, state.index) {
        if (state.playing) {
            delay((50 / state.speedMultiplier).toLong().coerceAtLeast(16L))
            engine.advanceFrame()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .focusable()
            .onKeyEvent { event ->
                val action = PlaybackInputHandler.actionFromKeyEvent(event)
                PlaybackInputHandler.apply(action, engine)
                action != dev.foss.expeditiongauge.playback.PlaybackInputAction.None
            },
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.playback_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        PlaybackMapPlaceholder(
            beta = sample?.driftAngleDeg,
            color = engine.driftColorForBeta(sample?.driftAngleDeg),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        )
        Slider(
            value = state.progress,
            onValueChange = { engine.seekProgress(it) },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            Button(onClick = { if (state.playing) engine.pause() else engine.play() }) {
                Text(
                    stringResource(
                        if (state.playing) R.string.playback_pause else R.string.playback_play,
                    ),
                )
            }
            Button(onClick = onBack) {
                Text(stringResource(R.string.settings_close))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(stringResource(R.string.playback_drift_analysis), color = GaugeScaleWhite)
            Switch(checked = state.showDriftAnalysis, onCheckedChange = { engine.toggleDriftAnalysis() })
        }
        if (state.showDriftAnalysis) {
            DriftAnalysisPanel(
                beta = sample?.driftAngleDeg,
                bodyYaw = sample?.bodyYawDeg,
                velocityHdg = sample?.velocityHeadingDeg,
                slipRatio = sample?.slipRatio,
            )
        }
        Text(
            text = stringResource(R.string.playback_keyboard_hint),
            color = GaugeYellow.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
        )
        PlaybackMetricsPanel(sample = sample)
    }
}

@Composable
private fun PlaybackMapPlaceholder(beta: Float?, color: Long, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(GaugeBackground),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.playback_map_beta, beta ?: 0f),
            color = Color(color),
        )
    }
}

@Composable
private fun DriftAnalysisPanel(
    beta: Float?,
    bodyYaw: Float?,
    velocityHdg: Float?,
    slipRatio: Float?,
) {
    Column {
        Text(stringResource(R.string.playback_beta, beta ?: 0f), color = GaugeScaleWhite)
        Text(stringResource(R.string.playback_body_yaw, bodyYaw ?: 0f), color = GaugeScaleWhite)
        Text(stringResource(R.string.playback_velocity_hdg, velocityHdg ?: 0f), color = GaugeScaleWhite)
        Text(stringResource(R.string.playback_slip_ratio, slipRatio ?: 0f), color = GaugeScaleWhite)
    }
}

@Composable
private fun PlaybackMetricsPanel(sample: SampleEntity?) {
    if (sample == null) return
    Column {
        Text(
            stringResource(R.string.playback_speed, sample.speedMps * 3.6f),
            color = GaugeYellow,
        )
        Text(
            stringResource(R.string.playback_lat_g, sample.latG),
            color = GaugeScaleWhite,
        )
        sample.rpm?.let {
            Text(stringResource(R.string.playback_rpm, it), color = GaugeScaleWhite)
        }
    }
}
