package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.export.PlaybackVideoExportScheduler
import dev.foss.expeditiongauge.export.PlaybackVideoExportSettings
import dev.foss.expeditiongauge.flyover.FlyoverVideoExportScheduler
import dev.foss.expeditiongauge.flyover.FlyoverVideoExportSettings
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.playback.PlaybackScreen
import dev.foss.expeditiongauge.ui.share.SharePreviewRequest
import dev.foss.expeditiongauge.ui.share.SharePreviewSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AppScreenPlaybackRoute(
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    statsSummaries: List<SessionStatsSummary>,
    onBack: () -> Unit,
) {
    val playbackState by services.playbackEngine.state.collectAsStateWithLifecycle()
    var sessionAlerts by remember { mutableStateOf<List<AlertEventEntity>>(emptyList()) }
    var hasVideo by remember { mutableStateOf(false) }
    var videoOffsetMs by remember { mutableLongStateOf(0L) }
    var exportPreset by remember { mutableStateOf(PlaybackVideoExportSettings.PRESET_120S) }
    val exportWorkName = playbackState.sessionId?.let { PlaybackVideoExportScheduler.workName(it) }
    val exportWorkInfos by (
        exportWorkName?.let { name ->
            WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(name)
        } ?: kotlinx.coroutines.flow.flowOf(emptyList())
        ).collectAsStateWithLifecycle(initialValue = emptyList())
    val exportWorkInfo = exportWorkInfos.firstOrNull()
    var flyoverPreset by remember { mutableStateOf(FlyoverVideoExportSettings.PRESET_30S) }
    val flyoverWorkName = playbackState.sessionId?.let { FlyoverVideoExportScheduler.workName(it) }
    val flyoverWorkInfos by (
        flyoverWorkName?.let { name ->
            WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(name)
        } ?: kotlinx.coroutines.flow.flowOf(emptyList())
        ).collectAsStateWithLifecycle(initialValue = emptyList())
    val flyoverWorkInfo = flyoverWorkInfos.firstOrNull()
    var sharePreviewRequest by remember { mutableStateOf<SharePreviewRequest?>(null) }
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
                        val output = File(context.cacheDir, "exports/session_${sessionId}_burnin.mp4")
                        output.parentFile?.mkdirs()
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
        exportPreset = exportPreset,
        onExportPresetChange = { exportPreset = it },
        exportWorkInfo = exportWorkInfo,
        onStartPlaybackExport = if (FeatureFlags.playbackVideoExportEnabled) {
            {
                playbackState.sessionId?.let { sessionId ->
                    PlaybackVideoExportScheduler.enqueue(context, sessionId, exportPreset)
                }
            }
        } else {
            null
        },
        onSharePlaybackExport = if (FeatureFlags.playbackVideoExportEnabled) {
            { path ->
                resolveVideoShare(context, path, playbackState.sessionId, statsSummaries) {
                    sharePreviewRequest = it
                }
            }
        } else {
            null
        },
        flyoverPreset = flyoverPreset,
        onFlyoverPresetChange = { flyoverPreset = it },
        flyoverWorkInfo = flyoverWorkInfo,
        onStartFlyoverExport = if (FeatureFlags.flyover3dEnabled) {
            {
                playbackState.sessionId?.let { sessionId ->
                    FlyoverVideoExportScheduler.enqueue(context, sessionId, flyoverPreset)
                }
            }
        } else {
            null
        },
        onShareFlyoverExport = if (FeatureFlags.flyover3dEnabled) {
            { path ->
                resolveVideoShare(context, path, playbackState.sessionId, statsSummaries) {
                    sharePreviewRequest = it
                }
            }
        } else {
            null
        },
        sessionMediaRepository = if (FeatureFlags.mediaAttachmentsEnabled) {
            services.sessionMediaRepository
        } else {
            null
        },
        onBack = onBack,
    )
    sharePreviewRequest?.let { request ->
        SharePreviewSheet(
            request = request,
            onDismiss = { sharePreviewRequest = null },
            onShared = { sharePreviewRequest = null },
        )
    }
}
