package dev.foss.expeditiongauge.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.foss.expeditiongauge.calibration.AutocalibrationController
import dev.foss.expeditiongauge.calibration.CalibrationStore
import android.content.Context
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.map.SessionMapPrefetch
import dev.foss.expeditiongauge.live.LivePairingSession
import dev.foss.expeditiongauge.live.LiveTelemetryModule
import dev.foss.expeditiongauge.alerts.AlertService
import dev.foss.expeditiongauge.car.gauge.next
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.nextAttitudeDisplay
import dev.foss.expeditiongauge.gauge.toAttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.toInclinometerStyle
import dev.foss.expeditiongauge.car.gauge.next
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.batterysaverrecord.BatterySaverRecord
import dev.foss.expeditiongauge.recording.RecordingMode
import dev.foss.expeditiongauge.recording.RecordingWriter
import dev.foss.expeditiongauge.recording.SessionEventRecorder
import dev.foss.expeditiongauge.settings.BatterySaverRecordStore
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.timing.LapTimingService
import dev.foss.expeditiongauge.timing.PredictiveTimingState
import dev.foss.expeditiongauge.settings.SettingsProfile
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.thermal.ThermalMonitor
import dev.foss.expeditiongauge.thermal.ThermalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val telemetry: TelemetrySnapshot = TelemetrySnapshot.empty(),
    val thermalStatus: ThermalStatus = ThermalStatus.Normal,
    val showDriftAngle: Boolean = false,
    val recording: Boolean = false,
    val activeSessionId: Long? = null,
    val activePreset: DashboardPreset = DashboardPreset.Default,
    val recordingMode: RecordingMode = RecordingMode.NORMAL,
    val activeAlerts: Set<dev.foss.expeditiongauge.alerts.AlertType> = emptySet(),
    val liveSession: LivePairingSession? = null,
    val liveReceiverCount: Int = 0,
    val isLive: Boolean = false,
    val displayRotation: Int = 0,
    val storageBlocked: Boolean = false,
)

class DashboardViewModel(
    private val appContext: Context,
    private val database: ExpeditionGaugeDatabase,
    private val telemetryBus: TelemetryBus,
    private val calibrationStore: CalibrationStore,
    private val autocalibrationController: AutocalibrationController,
    private val thermalMonitor: ThermalMonitor,
    private val recordingWriter: RecordingWriter,
    private val settingsProfileRepository: SettingsProfileRepository,
    private val sessionEventRecorder: SessionEventRecorder,
    private val liveTelemetryModule: LiveTelemetryModule,
    private val lapTimingService: LapTimingService,
    private val settingsPreferences: SettingsPreferences,
    private val alertService: AlertService,
    private val phoneSensorProvider: dev.foss.expeditiongauge.sensors.PhoneSensorProvider,
) : ViewModel() {
    private val liveState = MutableStateFlow(LiveUi(session = null, receiverCount = 0))
    private val displayRotation = MutableStateFlow(0)
    private var receiverCountJob: Job? = null

    val lapTimingState: StateFlow<PredictiveTimingState> = lapTimingService.liveState
    val autocalPending = autocalibrationController.pending
    val autocalMessages = autocalibrationController.messages

    val uiState: StateFlow<DashboardUiState> = combine(
        combine(
            telemetryBus.snapshots,
            thermalMonitor.status,
            recordingWriter.recording,
            recordingWriter.activeSessionId,
        ) { telemetry, thermal, recording, sessionId ->
            CoreState(telemetry, thermal, recording, sessionId)
        },
        combine(
            settingsProfileRepository.activeProfile,
            liveState,
            alertService.activeAlerts,
            displayRotation,
            recordingWriter.storageBlocked,
        ) { profile, live, alerts, rotation, storageBlocked ->
            AuxState(profile, live, alerts, rotation, storageBlocked)
        },
    ) { core, aux ->
        val preset = aux.profile.dashboardPreset
        DashboardUiState(
            telemetry = core.telemetry,
            thermalStatus = core.thermal,
            showDriftAngle = preset.showDriftAngle &&
                (core.telemetry.driftAngleDeg != null && core.telemetry.speedMps > 2f),
            recording = core.recording,
            activeSessionId = core.sessionId,
            activePreset = preset,
            recordingMode = aux.profile.recordingMode,
            activeAlerts = aux.alerts,
            liveSession = aux.live.session,
            liveReceiverCount = aux.live.receiverCount,
            isLive = aux.live.session != null,
            displayRotation = aux.rotation,
            storageBlocked = aux.storageBlocked,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(),
    )

    init {
        thermalMonitor.refresh()
        viewModelScope.launch { settingsProfileRepository.ensureDefaultProfile() }
    }

    fun refreshThermal() = thermalMonitor.refresh()

    fun updateDisplayRotation(rotation: Int) {
        displayRotation.value = rotation
        phoneSensorProvider.updateDisplayRotation(rotation)
    }

    fun calibrateLevel() {
        viewModelScope.launch {
            val snapshot = telemetryBus.snapshots.value
            autocalibrationController.manualZero(
                pitchDeg = snapshot.pitchDeg,
                rollDeg = snapshot.rollDeg,
                yawDeg = snapshot.bodyYawDeg ?: snapshot.headingDeg,
                displayRotation = displayRotation.value,
            )
        }
    }

    fun acceptAutocal() {
        viewModelScope.launch {
            autocalibrationController.acceptPending(displayRotation.value)
        }
    }

    fun dismissAutocal() {
        viewModelScope.launch {
            autocalibrationController.dismissPending()
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            val profile = settingsProfileRepository.activeProfile.first()
            val externalImu = telemetryBus.snapshots.value.imuStatuses.any { it.connected }
            val saverOn = BatterySaverRecordStore(appContext).enabled.first()
            BatterySaverRecord.active = saverOn
            runCatching {
                recordingWriter.startRecording(
                    recordingMode = profile.recordingMode,
                    externalImuConnected = externalImu,
                    manualStart = true,
                )
            }.onSuccess { sessionId ->
                BatterySaverRecord.applyInterval(saverOn, recordingWriter::setLogIntervalMs)
                if (settingsPreferences.lapTimingEnabled.first()) {
                    lapTimingService.onRecordingStarted(sessionId)
                }
            }.onFailure {
                BatterySaverRecord.active = false
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            val sessionId = recordingWriter.activeSessionId.value
            recordingWriter.stopRecording()
            BatterySaverRecord.active = false
            if (sessionId != null && settingsPreferences.lapTimingEnabled.first()) {
                lapTimingService.onRecordingStopped(sessionId)
            }
            sessionId?.let { id ->
                SessionMapPrefetch.enqueueAfterRecording(appContext, database, id)
            }
        }
    }

    fun selectPreset(presetId: DashboardPresetId) {
        viewModelScope.launch {
            settingsProfileRepository.updatePresetForActiveProfile(presetId)
            gaugeModeForPreset(presetId)?.let { settingsPreferences.setAttitudeGaugeMode(it) }
        }
    }

    fun toggleAttitudeDisplay() {
        viewModelScope.launch {
            val next = settingsPreferences.attitudeGaugeMode.first().nextAttitudeDisplay()
            settingsPreferences.setAttitudeGaugeMode(next)
            next.toInclinometerStyle()?.let { settingsPreferences.setInclinometerStyle(it) }
        }
    }

    fun cycleInclinometerStyle() {
        viewModelScope.launch {
            val nextStyle = settingsPreferences.inclinometerStyle.first().next()
            settingsPreferences.setInclinometerStyle(nextStyle)
            settingsPreferences.setAttitudeGaugeMode(nextStyle.toAttitudeGaugeMode())
        }
    }

    fun markEvent() {
        val sessionId = recordingWriter.activeSessionId.value ?: return
        viewModelScope.launch {
            sessionEventRecorder.markEvent(sessionId, telemetryBus.snapshots.value)
        }
    }

    fun startLiveSession() {
        if (!liveTelemetryModule.isEnabled()) return
        viewModelScope.launch {
            val url = settingsPreferences.liveSignalWssUrl.first()
            val session = liveTelemetryModule.pairingManager.createSession(url)
            liveState.value = LiveUi(session = session, receiverCount = 0)
            liveTelemetryModule.startSender(viewModelScope, session)
            receiverCountJob?.cancel()
            receiverCountJob = viewModelScope.launch {
                liveTelemetryModule.receiverCount.collectLatest { count ->
                    liveState.update { it.copy(receiverCount = count) }
                }
            }
        }
    }

    fun stopLiveSession() {
        receiverCountJob?.cancel()
        receiverCountJob = null
        liveTelemetryModule.stopSender()
        liveState.value = LiveUi(session = null, receiverCount = 0)
    }

    private data class CoreState(
        val telemetry: TelemetrySnapshot,
        val thermal: ThermalStatus,
        val recording: Boolean,
        val sessionId: Long?,
    )

    private data class AuxState(
        val profile: SettingsProfile,
        val live: LiveUi,
        val alerts: Set<dev.foss.expeditiongauge.alerts.AlertType>,
        val rotation: Int,
        val storageBlocked: Boolean,
    )

    private data class LiveUi(val session: LivePairingSession?, val receiverCount: Int)
}

internal fun gaugeModeForPreset(presetId: DashboardPresetId): AttitudeGaugeMode? =
    if (presetId == DashboardPresetId.Offroad) AttitudeGaugeMode.INCLINOMETER_LADDER else null
