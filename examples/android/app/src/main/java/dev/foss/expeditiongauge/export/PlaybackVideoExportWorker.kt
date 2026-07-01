package dev.foss.expeditiongauge.export

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import java.io.File

class PlaybackVideoExportWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val sessionId = inputData.getLong(KEY_SESSION_ID, -1L)
        val clipDurationMs = inputData.getLong(KEY_CLIP_DURATION_MS, 120_000L)
        if (sessionId < 0) return Result.failure()

        val database = ExpeditionGaugeDatabase.create(applicationContext)
        val samples = database.sampleDao().getBySession(sessionId)
        if (samples.isEmpty()) return Result.failure()

        val settings = PlaybackVideoExportSettings(clipDurationMs = clipDurationMs)
        val outputDir = File(applicationContext.cacheDir, "exports").apply { mkdirs() }
        val outputFile = File(outputDir, "session_${sessionId}_playback.mp4")

        val exporter = PlaybackVideoExporter()
        val exportResult = exporter.export(
            samples = samples,
            settings = settings,
            outputFile = outputFile,
            onProgress = { percent ->
                setProgressAsync(workDataOfProgress(percent))
            },
        )

        return exportResult.fold(
            onSuccess = {
                Result.success(
                    Data.Builder()
                        .putString(KEY_OUTPUT_PATH, outputFile.absolutePath)
                        .build(),
                )
            },
            onFailure = { Result.failure() },
        )
    }

    companion object {
        const val KEY_SESSION_ID = "session_id"
        const val KEY_CLIP_DURATION_MS = "clip_duration_ms"
        const val KEY_OUTPUT_PATH = "output_path"
        const val KEY_PROGRESS = "progress"
        const val WORK_NAME_PREFIX = "playback_video_export_"

        fun workDataOfProgress(percent: Int): Data =
            Data.Builder().putInt(KEY_PROGRESS, percent.coerceIn(0, 100)).build()
    }
}
