package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.drivingline.DrivingLineAnalyzer
import dev.foss.expeditiongauge.ghost.GhostLapOverlay
import dev.foss.expeditiongauge.playback.HeatmapMetric
import dev.foss.expeditiongauge.playback.PlaybackLayoutState
import dev.foss.expeditiongauge.playback.PlaybackEngine
import dev.foss.expeditiongauge.playback.PlaybackInputHandler
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.timing.LapTimingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun PlaybackScreen(
    engine: PlaybackEngine,
    lapTimingService: LapTimingService,
    settingsProfileRepository: SettingsProfileRepository? = null,
    sessionAlerts: List<AlertEventEntity> = emptyList(),
    videoSyncEngine: dev.foss.expeditiongauge.video.VideoSyncEngine? = null,
    hasVideo: Boolean = false,
    videoOffsetMs: Long = 0L,
    onImportVideo: (() -> Unit)? = null,
    onExportBurnIn: (() -> Unit)? = null,
    onVideoOffsetChange: ((Long) -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by engine.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sample = state.currentSample
    var heatmapMetric by remember { mutableStateOf(HeatmapMetric.DRIFT_ANGLE) }
    var lapSummary by remember { mutableStateOf<dev.foss.expeditiongauge.timing.LapTimingSummary?>(null) }
    val drivingLine = remember(state.samples) { DrivingLineAnalyzer().analyze(state.samples) }
    val (primaryLap, ghostLap) = resolveGhostCompareLaps(
        summary = lapSummary,
        samples = state.samples,
        currentIndex = state.currentIndex,
        ghostLapNumber = state.ghostLapNumber,
    )

    LaunchedEffect(state.sessionId) {
        lapSummary = state.sessionId?.let { lapTimingService.loadSummary(it) }
    }

    LaunchedEffect(settingsProfileRepository, state.sessionId) {
        if (settingsProfileRepository == null || !FeatureFlags.playbackLayoutEnabled) return@LaunchedEffect
        val profile = settingsProfileRepository.activeProfile.first()
        engine.applyLayout(
            PlaybackLayoutState(
                mapWeight = profile.playbackMapWeight,
                graphsExpanded = profile.playbackGraphsExpanded,
            ),
        )
    }

    LaunchedEffect(state.sessionId, state.showGhost, lapSummary, state.samples) {
        if (!state.showGhost) {
            engine.clearGhost()
            return@LaunchedEffect
        }
        if (state.ghostTrackMismatch || state.ghostSamples.isNotEmpty()) return@LaunchedEffect
        val summary = lapSummary ?: return@LaunchedEffect
        val best = lapTimingService.bestLap(summary.laps) ?: return@LaunchedEffect
        val ghostSamples = GhostLapOverlay().samplesForLap(state.samples, best)
        engine.loadGhost(ghostSamples, best.lapNumber)
    }

    LaunchedEffect(state.isPlaying, state.currentIndex, state.speedMultiplier) {
        if (state.isPlaying) {
            delay((50 / state.speedMultiplier).toLong().coerceAtLeast(16L))
            engine.advanceFrame()
        }
    }

    LaunchedEffect(state.currentSample?.timestampMs, state.isPlaying) {
        val sample = state.currentSample ?: return@LaunchedEffect
        videoSyncEngine?.seekVideoToPlaybackPosition(sample.timestampMs)
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
            modifier = Modifier.testTag("playback_title"),
        )
        Text(
            text = stringResource(
                R.string.playback_index_chip,
                state.currentIndex,
                state.samples.lastIndex.coerceAtLeast(0),
            ),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .testTag("playback_index_chip")
                .semantics {
                    contentDescription =
                        "playback index ${state.currentIndex} of ${state.samples.lastIndex.coerceAtLeast(0)}"
                },
        )
        PlaybackOverlayControls(
            state = state,
            onToggleRoute = { engine.toggleShowRoute() },
            onToggleDrivingLine = { engine.toggleDrivingLine() },
            onToggleGhost = { engine.toggleGhost() },
        )
        if (FeatureFlags.videoSyncEnabled && videoSyncEngine != null && onImportVideo != null) {
            PlaybackVideoControls(
                videoSyncEngine = videoSyncEngine,
                hasVideo = hasVideo,
                videoOffsetMs = videoOffsetMs,
                onImportVideo = onImportVideo,
                onExportBurnIn = onExportBurnIn ?: {},
                onVideoOffsetChange = onVideoOffsetChange ?: {},
            )
        }
        if (FeatureFlags.playbackLayoutEnabled) {
            PlaybackLayoutControls(
                mapWeight = state.mapWeight,
                graphsExpanded = state.graphsExpanded,
                graphsToggleEnabled = FeatureFlags.telemetryGraphsEnabled,
                onMapWeightPreset = { weight ->
                    engine.setMapWeight(weight)
                    settingsProfileRepository?.let { repo ->
                        scope.launch {
                            repo.updatePlaybackLayout(weight, state.graphsExpanded)
                        }
                    }
                },
                onGraphsExpandedChange = { expanded ->
                    engine.setGraphsExpanded(expanded)
                    settingsProfileRepository?.let { repo ->
                        scope.launch {
                            repo.updatePlaybackLayout(state.mapWeight, expanded)
                        }
                    }
                },
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            modifier = Modifier.semantics { contentDescription = "Drift Analysis" },
        ) {
            Text(stringResource(R.string.playback_drift_analysis), color = GaugeScaleWhite)
            Switch(
                checked = state.showDriftAnalysis,
                onCheckedChange = { engine.toggleDriftAnalysis() },
                modifier = Modifier.testTag("playback_drift_toggle"),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Box(
                modifier = Modifier
                    .weight(state.mapWeight)
                    .fillMaxHeight(),
            ) {
                if (state.samples.isNotEmpty()) {
                    PlaybackMapView(
                        samples = state.samples,
                        currentIndex = state.currentIndex,
                        heatmapEnabled = FeatureFlags.heatmapOverlayEnabled,
                        heatmapMetric = heatmapMetric,
                        showRoute = state.showRoute,
                        showDrivingLine = state.showDrivingLine && FeatureFlags.drivingLineEnabled,
                        drivingLine = drivingLine,
                        showGhost = state.showGhost && FeatureFlags.ghostLapEnabled,
                        ghostSamples = state.ghostSamples,
                        sectorLinesGeoJson = state.sectorLinesGeoJson,
                        showSectorBoundaries = lapSummary != null,
                        modifier = Modifier.fillMaxSize(),
                    )
                    VehicleDriftOverlay(
                        beta = sample?.driftAngleDeg,
                        bodyYawDeg = sample?.bodyYawDeg,
                        velocityHeadingDeg = sample?.velocityHeadingDeg,
                        enabled = state.showDriftAnalysis,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f - state.mapWeight)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                PlaybackMetricsPanel(sample = sample)
                LapListPanel(summary = lapSummary)
                if (state.showGhost && FeatureFlags.ghostLapEnabled) {
                    GhostLapComparePanel(
                        summary = lapSummary,
                        primaryLap = primaryLap,
                        ghostLap = ghostLap,
                        ghostDeltaMs = state.ghostDeltaMs,
                        trackMismatch = state.ghostTrackMismatch,
                        ghostLapNumber = state.ghostLapNumber,
                    )
                }
                if (state.showDriftAnalysis) {
                    DriftAnalysisCanvas(sample = sample)
                }
                AlertSummaryPanel(alerts = sessionAlerts)
            }
        }
        PlaybackBottomSection(
            state = state,
            engine = engine,
            heatmapMetric = heatmapMetric,
            onHeatmapMetricChange = { heatmapMetric = it },
            onBack = onBack,
        )
    }
}
