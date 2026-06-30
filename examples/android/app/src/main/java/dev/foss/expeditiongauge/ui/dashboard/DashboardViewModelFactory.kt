package dev.foss.expeditiongauge.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.live.LiveTelemetryModule
import dev.foss.expeditiongauge.recording.RecordingWriter
import dev.foss.expeditiongauge.recording.SessionEventRecorder
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.thermal.ThermalMonitor

class DashboardViewModelFactory(
    private val telemetryBus: TelemetryBus,
    private val calibrationStore: CalibrationStore,
    private val thermalMonitor: ThermalMonitor,
    private val recordingWriter: RecordingWriter,
    private val settingsProfileRepository: SettingsProfileRepository,
    private val sessionEventRecorder: SessionEventRecorder,
    private val liveTelemetryModule: LiveTelemetryModule,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                telemetryBus = telemetryBus,
                calibrationStore = calibrationStore,
                thermalMonitor = thermalMonitor,
                recordingWriter = recordingWriter,
                settingsProfileRepository = settingsProfileRepository,
                sessionEventRecorder = sessionEventRecorder,
                liveTelemetryModule = liveTelemetryModule,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
