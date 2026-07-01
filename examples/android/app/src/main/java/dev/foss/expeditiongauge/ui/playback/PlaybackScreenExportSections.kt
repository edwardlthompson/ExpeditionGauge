package dev.foss.expeditiongauge.ui.playback

import androidx.compose.runtime.Composable
import androidx.work.WorkInfo
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.export.PlaybackVideoExportSettings
import dev.foss.expeditiongauge.flyover.FlyoverVideoExportSettings
import dev.foss.expeditiongauge.playback.PlaybackState
import dev.foss.expeditiongauge.video.VideoSyncEngine

@Composable
fun PlaybackScreenVideoAndExportSections(
    state: PlaybackState,
    videoSyncEngine: VideoSyncEngine?,
    hasVideo: Boolean,
    videoOffsetMs: Long,
    onImportVideo: (() -> Unit)?,
    onExportBurnIn: (() -> Unit)?,
    onVideoOffsetChange: ((Long) -> Unit)?,
    exportPreset: PlaybackVideoExportSettings,
    onExportPresetChange: ((PlaybackVideoExportSettings) -> Unit)?,
    exportWorkInfo: WorkInfo?,
    onStartPlaybackExport: (() -> Unit)?,
    onSharePlaybackExport: ((String) -> Unit)?,
    flyoverPreset: FlyoverVideoExportSettings,
    onFlyoverPresetChange: ((FlyoverVideoExportSettings) -> Unit)?,
    flyoverWorkInfo: WorkInfo?,
    onStartFlyoverExport: (() -> Unit)?,
    onShareFlyoverExport: ((String) -> Unit)?,
) {
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
    if (FeatureFlags.playbackVideoExportEnabled && onStartPlaybackExport != null) {
        PlaybackExportPanel(
            sessionId = state.sessionId,
            selectedPreset = exportPreset,
            onPresetSelected = { onExportPresetChange?.invoke(it) },
            onStartExport = onStartPlaybackExport,
            onShareExport = { path -> onSharePlaybackExport?.invoke(path) },
            workInfo = exportWorkInfo,
        )
    }
    if (FeatureFlags.flyover3dEnabled && onStartFlyoverExport != null) {
        FlyoverExportPanel(
            sessionId = state.sessionId,
            selectedPreset = flyoverPreset,
            onPresetSelected = { onFlyoverPresetChange?.invoke(it) },
            onStartExport = onStartFlyoverExport,
            onShareExport = { path -> onShareFlyoverExport?.invoke(path) },
            workInfo = flyoverWorkInfo,
        )
    }
}
