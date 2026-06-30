package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

enum class SpeedUnit { METRIC, IMPERIAL }

class SettingsPreferences(private val context: Context) {
    private val trackGauge = TrackGaugePreferences(context)
    private val keys = SettingsPreferencesKeys

    val speedUnit: Flow<SpeedUnit> = context.settingsDataStore.data.map { keys.speedUnitFrom(it) }

    val logIntervalMs: Flow<Long> = context.settingsDataStore.data.map { prefs ->
        prefs[keys.logInterval] ?: dev.foss.expeditiongauge.recording.RecordingWriter.DEFAULT_LOG_INTERVAL_MS
    }

    val obdDeviceAddress: Flow<String?> = context.settingsDataStore.data.map { it[keys.obdDevice] }
    val externalGpsAddress: Flow<String?> = context.settingsDataStore.data.map { it[keys.externalGpsDevice] }
    val externalGpsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[keys.externalGpsEnabled] ?: false }

    val obdPidConfig: Flow<ObdPidConfig> = context.settingsDataStore.data.map { keys.obdPidConfigFrom(it) }

    val tpmsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[keys.tpmsEnabled] ?: false }
    val pressureUnit: Flow<PressureUnit> = context.settingsDataStore.data.map { keys.pressureUnitFrom(it) }
    val tempUnit: Flow<TempUnit> = context.settingsDataStore.data.map { keys.tempUnitFrom(it) }

    val lapTimingEnabled: Flow<Boolean> = trackGauge.lapTimingEnabled
    val trackStartFinishGeoJson: Flow<String?> = trackGauge.trackStartFinishGeoJson
    val trackSectorLinesGeoJson: Flow<String?> = trackGauge.trackSectorLinesGeoJson
    val attitudeGaugeMode: Flow<AttitudeGaugeMode> = trackGauge.attitudeGaugeMode

    val developerModeEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[keys.developerMode] ?: false
    }

    val madgwickBeta: Flow<Float> = context.settingsDataStore.data.map {
        it[keys.madgwickBeta] ?: 0.1f
    }

    val liveTelemetryEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[keys.liveTelemetry] ?: false
    }

    val liveSignalWssUrl: Flow<String> = context.settingsDataStore.data.map {
        it[keys.liveSignalWss] ?: dev.foss.expeditiongauge.live.LivePairingManager.DEFAULT_SIGNAL_WSS
    }

    suspend fun setSpeedUnit(unit: SpeedUnit) {
        context.settingsDataStore.edit {
            it[keys.speedUnit] = if (unit == SpeedUnit.IMPERIAL) "imperial" else "metric"
        }
    }

    suspend fun setLogIntervalMs(ms: Long) {
        context.settingsDataStore.edit { it[keys.logInterval] = ms }
    }

    suspend fun setObdDeviceAddress(address: String?) {
        context.settingsDataStore.edit {
            if (address == null) it.remove(keys.obdDevice) else it[keys.obdDevice] = address
        }
    }

    suspend fun setExternalGpsAddress(address: String?) {
        context.settingsDataStore.edit {
            if (address == null) it.remove(keys.externalGpsDevice) else it[keys.externalGpsDevice] = address
        }
    }

    suspend fun setExternalGpsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.externalGpsEnabled] = enabled }
    }

    suspend fun forgetExternalGpsDevice() {
        context.settingsDataStore.edit {
            it.remove(keys.externalGpsDevice)
            it[keys.externalGpsEnabled] = false
        }
    }

    suspend fun setTpmsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.tpmsEnabled] = enabled }
    }

    suspend fun setPressureUnit(unit: PressureUnit) {
        context.settingsDataStore.edit {
            it[keys.pressureUnit] = if (unit == PressureUnit.PSI) "psi" else "kpa"
        }
    }

    suspend fun setTempUnit(unit: TempUnit) {
        context.settingsDataStore.edit {
            it[keys.tempUnit] = if (unit == TempUnit.FAHRENHEIT) "fahrenheit" else "celsius"
        }
    }

    suspend fun setObdPidConfig(config: ObdPidConfig) {
        context.settingsDataStore.edit {
            it[keys.obdPidRpm] = config.rpm
            it[keys.obdPidSpeed] = config.speed
            it[keys.obdPidThrottle] = config.throttle
            it[keys.obdPidLoad] = config.load
            it[keys.obdPidVoltage] = config.voltage
            it[keys.obdPidRearWheels] = config.rearWheels
        }
    }

    suspend fun setLapTimingEnabled(enabled: Boolean) = trackGauge.setLapTimingEnabled(enabled)
    suspend fun setTrackStartFinishGeoJson(geoJson: String?) = trackGauge.setTrackStartFinishGeoJson(geoJson)
    suspend fun setTrackSectorLinesGeoJson(geoJson: String?) = trackGauge.setTrackSectorLinesGeoJson(geoJson)
    suspend fun setAttitudeGaugeMode(mode: AttitudeGaugeMode) = trackGauge.setAttitudeGaugeMode(mode)
    suspend fun clearTrackConfig() = trackGauge.clearTrackConfig()

    suspend fun setDeveloperModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.developerMode] = enabled }
    }

    suspend fun setMadgwickBeta(beta: Float) {
        context.settingsDataStore.edit { it[keys.madgwickBeta] = beta.coerceIn(0.01f, 0.5f) }
    }

    suspend fun setLiveTelemetryEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.liveTelemetry] = enabled }
    }

    suspend fun setLiveSignalWssUrl(url: String) {
        context.settingsDataStore.edit { it[keys.liveSignalWss] = url }
    }

    suspend fun resetCalibrationFlag() {
        context.settingsDataStore.edit { it[keys.calibrationReset] = 1f }
    }
}
