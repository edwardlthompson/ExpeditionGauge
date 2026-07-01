package dev.foss.expeditiongauge.flyover

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object FlyoverVideoExportScheduler {
    fun enqueue(context: Context, sessionId: Long, settings: FlyoverVideoExportSettings) {
        val request = OneTimeWorkRequestBuilder<FlyoverVideoExportWorker>()
            .setInputData(
                workDataOf(
                    FlyoverVideoExportWorker.KEY_SESSION_ID to sessionId,
                    FlyoverVideoExportWorker.KEY_CLIP_DURATION_MS to settings.clipDurationMs,
                ),
            )
            .addTag(WORK_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${FlyoverVideoExportWorker.WORK_NAME_PREFIX}$sessionId",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun workName(sessionId: Long): String = "${FlyoverVideoExportWorker.WORK_NAME_PREFIX}$sessionId"

    const val WORK_TAG = "flyover_video_export"
}
