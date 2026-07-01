package dev.foss.expeditiongauge.recording

import android.content.Context
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.media.SessionDeleteService
import dev.foss.expeditiongauge.media.SessionMediaRepository
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import kotlinx.coroutines.CoroutineScope

internal data class RecordingServiceBundle(
    val sessionMediaRepository: SessionMediaRepository,
    val sessionDeleteService: SessionDeleteService,
    val sessionStorageBudget: SessionStorageBudget,
    val recordingWriter: RecordingWriter,
    val autoRecordMonitor: AutoRecordMonitor,
)

internal fun createRecordingServices(
    appContext: Context,
    database: ExpeditionGaugeDatabase,
    telemetryBus: TelemetryBus,
    settingsPreferences: SettingsPreferences,
    scope: CoroutineScope,
): RecordingServiceBundle {
    val sessionMediaRepository = SessionMediaRepository(appContext, database.sessionMediaDao())
    val sessionDeleteService = SessionDeleteService(database.recordingSessionDao(), sessionMediaRepository)
    val sessionStorageBudget = SessionStorageBudget(
        appContext,
        database.recordingSessionDao(),
        sessionDeleteService,
        sessionMediaRepository,
        settingsPreferences,
    )
    val recordingWriter = RecordingWriter(telemetryBus, database, scope, storageBudget = sessionStorageBudget)
    val autoRecordMonitor = AutoRecordMonitor(appContext, settingsPreferences, recordingWriter, scope)
    return RecordingServiceBundle(
        sessionMediaRepository,
        sessionDeleteService,
        sessionStorageBudget,
        recordingWriter,
        autoRecordMonitor,
    )
}
