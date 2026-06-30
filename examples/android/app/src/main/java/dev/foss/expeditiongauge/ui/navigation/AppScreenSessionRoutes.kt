package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.export.EnhancedExportFormat
import dev.foss.expeditiongauge.export.HtmlSummaryExporter
import dev.foss.expeditiongauge.playback.PlaybackSessionLoader
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.stats.SessionComparison
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.playback.PlaybackScreen
import dev.foss.expeditiongauge.ui.playback.SessionListScreen
import dev.foss.expeditiongauge.ui.playback.SessionMetadataEditScreen
import dev.foss.expeditiongauge.ui.stats.SessionComparisonScreen
import dev.foss.expeditiongauge.ui.stats.SessionStatsDashboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AppScreenSessionRoutes(
    screen: AppScreen,
    onScreenChange: (AppScreen) -> Unit,
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    statsSummaries: List<SessionStatsSummary>,
    statsAggregate: SessionAggregateStats,
    comparison: MutableState<SessionComparison?>,
    sessionStatsAggregator: SessionStatsAggregator,
    editingSessionId: Long?,
    onEditingSessionIdChange: (Long?) -> Unit,
) {
    when (screen) {
        AppScreen.Sessions -> SessionListScreen(
            database = services.database,
            statsSummaries = statsSummaries,
            onSessionSelected = { sessionId ->
                scope.launch {
                    PlaybackSessionLoader.load(services, sessionId)
                    onScreenChange(AppScreen.Playback)
                }
            },
            onSessionCompare = { leftId, rightId ->
                val left = statsSummaries.firstOrNull { it.sessionId == leftId }
                val right = statsSummaries.firstOrNull { it.sessionId == rightId }
                if (left != null && right != null) {
                    comparison.value = sessionStatsAggregator.compare(left, right)
                    onScreenChange(AppScreen.Comparison)
                }
            },
            onSessionEdit = { sessionId ->
                onEditingSessionIdChange(sessionId)
                onScreenChange(AppScreen.SessionEdit)
            },
            onSessionExportZip = { sessionId ->
                scope.launch {
                    val file = services.enhancedExportService.exportSession(
                        sessionId,
                        EnhancedExportFormat.ZIP,
                        context.cacheDir,
                    )
                    shareExportFile(context, file, "application/zip")
                }
            },
            onBack = { onScreenChange(AppScreen.Dashboard) },
        )
        AppScreen.SessionEdit -> editingSessionId?.let { sessionId ->
            SessionMetadataEditScreen(
                sessionId = sessionId,
                repository = services.sessionMetadataRepository,
                context = context,
                onSaved = { onScreenChange(AppScreen.Sessions) },
                onBack = { onScreenChange(AppScreen.Sessions) },
            )
        }
        AppScreen.Playback -> {
            val playbackState by services.playbackEngine.state.collectAsStateWithLifecycle()
            var sessionAlerts by remember { mutableStateOf<List<AlertEventEntity>>(emptyList()) }
            var hasVideo by remember { mutableStateOf(false) }
            var videoOffsetMs by remember { mutableLongStateOf(0L) }
            val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let { picked ->
                    scope.launch {
                        services.videoSyncEngine.importVideo(picked.toString())
                        hasVideo = services.videoSyncEngine.videoUri != null
                    }
                }
            }
            LaunchedEffect(playbackState.sessionId) {
                sessionAlerts = playbackState.sessionId?.let { sessionId ->
                    services.database.alertEventDao().getBySession(sessionId)
                } ?: emptyList()
                playbackState.sessionId?.let { sessionId ->
                    services.videoSyncEngine.bindSession(sessionId)
                    hasVideo = services.videoSyncEngine.videoUri != null
                    videoOffsetMs = services.videoSyncEngine.videoOffsetMs
                }
            }
            PlaybackScreen(
                engine = services.playbackEngine,
                lapTimingService = services.lapTimingService,
                settingsProfileRepository = services.settingsProfileRepository,
                sessionAlerts = sessionAlerts,
                videoSyncEngine = if (FeatureFlags.videoSyncEnabled) services.videoSyncEngine else null,
                hasVideo = hasVideo,
                videoOffsetMs = videoOffsetMs,
                onImportVideo = { videoPicker.launch("video/*") },
                onExportBurnIn = {
                    scope.launch {
                        val uri = services.videoSyncEngine.videoUri ?: return@launch
                        val sessionId = playbackState.sessionId ?: return@launch
                        val output = File(context.cacheDir, "session_${sessionId}_burnin.mp4")
                        services.videoBurnInExporter.export(
                            videoUri = uri,
                            samples = playbackState.samples,
                            videoOffsetMs = videoOffsetMs,
                            outputFile = output,
                        ).onSuccess { file ->
                            shareExportFile(context, file, "video/mp4")
                        }
                    }
                },
                onVideoOffsetChange = { offset ->
                    videoOffsetMs = offset
                    scope.launch { services.videoSyncEngine.setOffsetMs(offset) }
                },
                onBack = { onScreenChange(AppScreen.Sessions) },
            )
        }
        AppScreen.Stats -> SessionStatsDashboard(
            sessions = statsSummaries,
            aggregate = statsAggregate,
            onPlay = { sessionId ->
                scope.launch {
                    PlaybackSessionLoader.load(services, sessionId)
                    onScreenChange(AppScreen.Playback)
                }
            },
            onCompare = { leftId, rightId ->
                val left = statsSummaries.first { it.sessionId == leftId }
                val right = statsSummaries.first { it.sessionId == rightId }
                comparison.value = sessionStatsAggregator.compare(left, right)
                onScreenChange(AppScreen.Comparison)
            },
            onExport = { summary ->
                scope.launch {
                    val events = services.database.sessionEventDao().getBySession(summary.sessionId)
                    shareHtmlSummary(context, HtmlSummaryExporter.export(summary, events))
                }
            },
            onBack = { onScreenChange(AppScreen.Dashboard) },
        )
        AppScreen.Comparison -> comparison.value?.let { cmp ->
            SessionComparisonScreen(
                comparison = cmp,
                onExport = {
                    shareHtmlSummary(context, HtmlSummaryExporter.exportComparison(cmp.left, cmp.right))
                },
                onGhostCompare = {
                    scope.launch {
                        PlaybackSessionLoader.loadWithGhost(services, cmp.left.sessionId, cmp.right.sessionId)
                        onScreenChange(AppScreen.Playback)
                    }
                },
                onBack = { onScreenChange(AppScreen.Stats) },
            )
        }
        else -> Unit
    }
}

internal fun shareHtmlSummary(context: Context, html: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/html"
        putExtra(Intent.EXTRA_TEXT, html)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.stats_export)))
}

internal fun shareExportFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.stats_export)))
}
