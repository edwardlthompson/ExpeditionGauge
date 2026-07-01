package dev.foss.expeditiongauge.export

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object PlaybackVideoExportScheduler {
    fun enqueue(context: Context, sessionId: Long, settings: PlaybackVideoExportSettings) {
        val request = OneTimeWorkRequestBuilder<PlaybackVideoExportWorker>()
            .setInputData(
                workDataOf(
                    PlaybackVideoExportWorker.KEY_SESSION_ID to sessionId,
                    PlaybackVideoExportWorker.KEY_CLIP_DURATION_MS to settings.clipDurationMs,
                ),
            )
            .addTag(WORK_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${PlaybackVideoExportWorker.WORK_NAME_PREFIX}$sessionId",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun workName(sessionId: Long): String = "${PlaybackVideoExportWorker.WORK_NAME_PREFIX}$sessionId"

    const val WORK_TAG = "playback_video_export"
}
