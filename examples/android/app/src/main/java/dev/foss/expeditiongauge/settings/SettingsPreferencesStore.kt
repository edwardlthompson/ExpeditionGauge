package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.recording.SessionStorageBudget

internal class SettingsPreferencesStore(
    private val context: Context,
    private val trackGauge: TrackGaugePreferences,
) {
    private val keys = SettingsPreferencesKeys
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
    suspend fun setInclinometerStyle(style: InclinometerStyle) = trackGauge.setInclinometerStyle(style)
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

    suspend fun setAndroidAutoEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.androidAutoEnabled] = enabled }
    }

    suspend fun setAndroidAutoMetricAllowlist(allowlist: Set<String>) {
        context.settingsDataStore.edit { it[keys.androidAutoMetrics] = allowlist.joinToString(",") }
    }

    suspend fun toggleAndroidAutoMetric(key: String) {
        context.settingsDataStore.edit { prefs ->
            val current = prefs[keys.androidAutoMetrics]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toMutableSet()
                ?: mutableSetOf()
            if (key in current) current.remove(key) else current.add(key)
            prefs[keys.androidAutoMetrics] = current.joinToString(",")
        }
    }

    suspend fun setMediaCompressionQuality(quality: MediaCompressionQuality) {
        context.settingsDataStore.edit { it[keys.mediaCompression] = quality.name }
    }

    suspend fun setAutoRecordEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.autoRecordEnabled] = enabled }
    }

    suspend fun setAutoRecordDeviceAddresses(addresses: Set<String>) {
        context.settingsDataStore.edit {
            if (addresses.isEmpty()) {
                it.remove(keys.autoRecordDevices)
            } else {
                it[keys.autoRecordDevices] = addresses.joinToString(",")
            }
        }
    }

    suspend fun setSessionStorageFreePercent(percent: Int) {
        val clamped = percent.coerceIn(SessionStorageBudget.MIN_PERCENT, SessionStorageBudget.MAX_PERCENT)
        context.settingsDataStore.edit { it[keys.sessionStorageFreePercent] = clamped }
    }

    suspend fun resetCalibrationFlag() {
        context.settingsDataStore.edit { it[keys.calibrationReset] = 1f }
    }

    suspend fun setAutoCalibrateWhenStill(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.autoCalibrateWhenStill] = enabled }
    }

    suspend fun setCoordFormatDecimal(decimal: Boolean) =
        context.settingsDataStore.edit { it[keys.coordFormatDecimal] = decimal }
}
