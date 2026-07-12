package dev.foss.expeditiongauge

import android.content.Context
import dev.foss.expeditiongauge.ble.BleConnectionBudget
import dev.foss.expeditiongauge.ble.BleImuManager
import dev.foss.expeditiongauge.ble.BleScanCoordinator
import dev.foss.expeditiongauge.ble.tpms.BleTpmsManager
import dev.foss.expeditiongauge.alerts.AlertService
import dev.foss.expeditiongauge.alerts.AlertThresholdsPreferences
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.calibration.AutocalibrationController
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.calibration.CalibrationWizardStore
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.export.EnhancedExportService
import dev.foss.expeditiongauge.export.ExportService
import dev.foss.expeditiongauge.video.DefaultVideoSyncEngine
import dev.foss.expeditiongauge.video.VideoBurnInExporter
import dev.foss.expeditiongauge.video.VideoSyncEngine
import dev.foss.expeditiongauge.video.VideoSyncEngineStub
import dev.foss.expeditiongauge.fusion.SensorFusionEngine
import dev.foss.expeditiongauge.gps.DemElevationLookup
import dev.foss.expeditiongauge.gps.ExternalNmeaGpsManager
import dev.foss.expeditiongauge.gps.FusedGpsLocationProvider
import dev.foss.expeditiongauge.live.LiveTelemetryModule
import dev.foss.expeditiongauge.map.HomeMapRegionPreferences
import dev.foss.expeditiongauge.map.MapOfflineDownloadManager
import dev.foss.expeditiongauge.map.MapTileCacheRepository
import dev.foss.expeditiongauge.obd.ClassicBluetoothBudget
import dev.foss.expeditiongauge.obd.ObdClassicManager
import dev.foss.expeditiongauge.playback.PlaybackEngine
import dev.foss.expeditiongauge.recording.RecordingWriter
import dev.foss.expeditiongauge.recording.SessionEventRecorder
import dev.foss.expeditiongauge.recording.SessionMetadataRepository
import dev.foss.expeditiongauge.recording.createRecordingServices
import dev.foss.expeditiongauge.timing.LapTimingService
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.sensors.PhoneSensorProvider
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetryOrchestrator
import dev.foss.expeditiongauge.thermal.ThermalMonitor
import kotlinx.coroutines.CoroutineScope

class ExpeditionGaugeServices(
    private val appContext: Context,
    scope: CoroutineScope,
) {
    val telemetryBus = TelemetryBus()
    val calibrationStore = CalibrationStore(appContext)
    val autocalibrationController = AutocalibrationController(calibrationStore)
    val fusionEngine = SensorFusionEngine(calibrationStore)
    val driftEstimator = DriftAngleEstimator()
    val thermalMonitor = ThermalMonitor(appContext)
    val settingsPreferences = SettingsPreferences(appContext)
    val homeMapRegionPreferences = HomeMapRegionPreferences(appContext)
    val mapTileCacheRepository = MapTileCacheRepository(appContext)
    val mapOfflineDownloadManager = MapOfflineDownloadManager(
        appContext,
        mapTileCacheRepository,
        homeMapRegionPreferences,
    )

    val scanCoordinator = BleScanCoordinator()
    val bleConnectionBudget = BleConnectionBudget()
    val classicBluetoothBudget = ClassicBluetoothBudget()

    val bleImuManager = BleImuManager(appContext, scanCoordinator, bleConnectionBudget)
    val bleTpmsManager = BleTpmsManager(appContext, scanCoordinator)
    val obdManager = ObdClassicManager(appContext, classicBluetoothBudget, scope)
    val externalGpsManager = ExternalNmeaGpsManager(appContext, classicBluetoothBudget, scope)

    lateinit var phoneSensorProvider: PhoneSensorProvider
    lateinit var fusedGpsProvider: FusedGpsLocationProvider
    lateinit var telemetryOrchestrator: TelemetryOrchestrator

    val database = ExpeditionGaugeDatabase.create(appContext)
    private val recordingBundle = createRecordingServices(appContext, database, telemetryBus, settingsPreferences, scope)
    val sessionMediaRepository get() = recordingBundle.sessionMediaRepository
    val sessionDeleteService get() = recordingBundle.sessionDeleteService
    val sessionStorageBudget get() = recordingBundle.sessionStorageBudget
    val recordingWriter get() = recordingBundle.recordingWriter
    val autoRecordMonitor get() = recordingBundle.autoRecordMonitor
    val exportService = ExportService(database)
    val enhancedExportService = EnhancedExportService(database, exportService, appContext)
    val videoSyncEngine: VideoSyncEngine = if (FeatureFlags.videoSyncEnabled) {
        DefaultVideoSyncEngine(appContext, database.recordingSessionDao())
    } else {
        VideoSyncEngineStub()
    }
    val videoBurnInExporter = VideoBurnInExporter(appContext)
    val calibrationWizardStore = CalibrationWizardStore(appContext)
    val playbackEngine = PlaybackEngine()
    val settingsProfileRepository = SettingsProfileRepository(appContext, database.settingsProfileDao())
    val sessionEventRecorder = SessionEventRecorder(database.sessionEventDao())
    val sessionMetadataRepository = SessionMetadataRepository(database.recordingSessionDao())
    val lapTimingService = LapTimingService(database, settingsPreferences)
    val liveTelemetryModule = LiveTelemetryModule(telemetryBus)
    val alertThresholdsPreferences = AlertThresholdsPreferences(appContext)
    lateinit var alertService: AlertService

    fun initialize(scope: CoroutineScope, accessibilityPreferences: AccessibilityPreferences) {
        alertService = AlertService(
            context = appContext,
            alertEventDao = database.alertEventDao(),
            thresholdsPreferences = alertThresholdsPreferences,
            scope = scope,
        )
        phoneSensorProvider = PhoneSensorProvider(
            context = appContext,
            fusionEngine = fusionEngine,
            driftEstimator = driftEstimator,
            telemetryBus = telemetryBus,
            calibrationStore = calibrationStore,
            scope = scope,
            bleImuManager = bleImuManager,
            autocalibrationController = autocalibrationController,
        )
        fusedGpsProvider = FusedGpsLocationProvider(
            context = appContext,
            telemetryBus = telemetryBus,
            driftEstimator = driftEstimator,
            sensorProvider = phoneSensorProvider,
            externalGps = externalGpsManager,
            demElevation = DemElevationLookup(scope),
        )
        telemetryOrchestrator = TelemetryOrchestrator(
            telemetryBus = telemetryBus,
            bleImuManager = bleImuManager,
            bleTpmsManager = bleTpmsManager,
            obdManager = obdManager,
            externalGps = externalGpsManager,
            fusedGps = fusedGpsProvider,
            scope = scope,
        )
        telemetryOrchestrator.start()
        autoRecordMonitor.start()
        bindLifecycleFlows(scope, accessibilityPreferences)
    }
}
