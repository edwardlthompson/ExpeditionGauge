package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.export.ExportExtrasParser
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.playback.HeatmapMetric
import dev.foss.expeditiongauge.playback.PlaybackEngine
import dev.foss.expeditiongauge.playback.RouteHeatmapLayer
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.layout.navigationBarBottomPadding
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
internal fun PlaybackBottomSection(
    state: dev.foss.expeditiongauge.playback.PlaybackState,
    engine: PlaybackEngine,
    heatmapMetric: HeatmapMetric,
    onHeatmapMetricChange: (HeatmapMetric) -> Unit,
    speedUnit: SpeedUnit = SpeedUnit.METRIC,
    onBack: () -> Unit,
    onMediaMarkerTap: ((dev.foss.expeditiongauge.playback.ScrubberMarker) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.navigationBarBottomPadding(),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        if (FeatureFlags.elevationProfileEnabled) {
            ElevationProfilePanel(
                state = state,
                speedUnit = speedUnit,
                onSeek = { engine.seekToIndex(it) },
            )
        }
        if (FeatureFlags.telemetryGraphsEnabled && state.samples.isNotEmpty() && state.graphsExpanded) {
            TelemetryGraphPanel(
                state = state,
                speedUnit = speedUnit,
                onSeek = { engine.seekToIndex(it) },
            )
        }
        if (FeatureFlags.heatmapOverlayEnabled) {
            RouteHeatmapControls(selected = heatmapMetric, onSelect = onHeatmapMetricChange)
            state.currentSample?.let { sample ->
                HeatmapLegend(
                    intensity = RouteHeatmapLayer.sampleIntensity(sample, heatmapMetric),
                    metric = heatmapMetric,
                )
            }
        }
        if (state.markers.isNotEmpty()) {
            ScrubberMarkerStrip(
                markers = state.markers,
                totalSamples = state.samples.size,
                onSeek = { engine.seekToIndex(it) },
                onMediaMarkerTap = onMediaMarkerTap,
            )
        }
        Slider(
            value = state.progress,
            onValueChange = { engine.seekProgress(it) },
            modifier = Modifier.fillMaxWidth().testTag("playback_scrubber"),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            Button(onClick = { engine.togglePlayPause() }) {
                Text(
                    stringResource(
                        if (state.isPlaying) R.string.playback_pause else R.string.playback_play,
                    ),
                )
            }
            Button(onClick = { engine.adjustSpeed(0.25f) }) {
                Text(stringResource(R.string.playback_speed_up))
            }
            Button(onClick = { engine.adjustSpeed(-0.25f) }) {
                Text(stringResource(R.string.playback_speed_down))
            }
            Button(onClick = onBack) {
                Text(stringResource(R.string.settings_close))
            }
        }
        Text(
            text = stringResource(
                R.string.playback_status,
                state.currentIndex,
                state.samples.lastIndex.coerceAtLeast(0),
                if (state.isPlaying) "▶" else "⏸",
                state.speedMultiplier,
            ),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .testTag("playback_status")
                .semantics {
                    contentDescription =
                        "playback index ${state.currentIndex} of ${state.samples.lastIndex.coerceAtLeast(0)}"
                },
        )
        Text(
            text = stringResource(R.string.playback_keyboard_hint),
            color = GaugeYellow.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
internal fun PlaybackMetricsPanel(
    sample: SampleEntity?,
    speedUnit: SpeedUnit = SpeedUnit.METRIC,
    pressureUnit: PressureUnit = PressureUnit.KPA,
) {
    if (sample == null) {
        Text(stringResource(R.string.playback_no_sample), color = GaugeScaleWhite)
        return
    }
    val gps = dev.foss.expeditiongauge.playback.SampleGpsMetadata.fromSample(sample)
    val tpms = ExportExtrasParser.tpmsColumns(sample.extrasJson)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            stringResource(R.string.playback_beta, sample.driftAngleDeg ?: 0f),
            color = GaugeYellow,
            modifier = Modifier
                .testTag("playback_beta_readout")
                .semantics { contentDescription = "beta ${sample.driftAngleDeg ?: 0f}" },
        )
        Text(
            stringResource(
                R.string.playback_speed,
                "${UnitDisplay.speedMpsToDisplay(sample.speedMps, speedUnit).toInt()} ${UnitDisplay.speedUnitLabel(speedUnit)}",
            ),
            color = GaugeScaleWhite,
        )
        Text(stringResource(R.string.playback_lat_g, sample.latG), color = GaugeScaleWhite)
        Text(stringResource(R.string.playback_slip_ratio, sample.slipRatio ?: 0f), color = GaugeScaleWhite)
        sample.throttle?.let { Text(stringResource(R.string.playback_throttle, it), color = GaugeScaleWhite) }
        sample.rpm?.let { Text(stringResource(R.string.playback_rpm, it), color = GaugeScaleWhite) }
        Text(
            stringResource(
                R.string.playback_gps_source,
                gps.gpsSource.uppercase(),
                gps.numSatellites ?: 0,
                gps.hdop ?: 0f,
            ),
            color = if (dev.foss.expeditiongauge.playback.SampleGpsMetadata.prefersExternal(sample)) {
                GaugeYellow
            } else {
                GaugeScaleWhite
            },
            style = MaterialTheme.typography.labelSmall,
        )
        if (tpms.hasAnyData) {
            Text(
                stringResource(
                    R.string.playback_tpms_fl,
                    formatPlaybackPressure(tpms.frontLeft.pressureKpa, pressureUnit),
                ),
                color = GaugeScaleWhite,
            )
            Text(
                stringResource(
                    R.string.playback_tpms_fr,
                    formatPlaybackPressure(tpms.frontRight.pressureKpa, pressureUnit),
                ),
                color = GaugeScaleWhite,
            )
            Text(
                stringResource(
                    R.string.playback_tpms_rl,
                    formatPlaybackPressure(tpms.rearLeft.pressureKpa, pressureUnit),
                ),
                color = GaugeScaleWhite,
            )
            Text(
                stringResource(
                    R.string.playback_tpms_rr,
                    formatPlaybackPressure(tpms.rearRight.pressureKpa, pressureUnit),
                ),
                color = GaugeScaleWhite,
            )
        }
        if (dev.foss.expeditiongauge.playback.SampleImuExtras.hasMultiImu(sample.extrasJson)) {
            Text(stringResource(R.string.playback_multi_imu), color = GaugeYellow)
        }
    }
}

private fun formatPlaybackPressure(kpa: Float?, unit: PressureUnit): String {
    if (kpa == null) return "--"
    val display = UnitDisplay.pressureKpaToDisplay(kpa, unit)
    val label = UnitDisplay.pressureUnitLabel(unit)
    return if (unit == PressureUnit.KPA) "${display.toInt()} $label" else String.format("%.1f %s", display, label)
}
