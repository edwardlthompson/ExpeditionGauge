package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

enum class SpeedUnit { METRIC, IMPERIAL }

class SettingsPreferences(private val context: Context) {
    private val speedUnitKey = stringPreferencesKey("speed_unit")
    private val logIntervalKey = longPreferencesKey("log_interval_ms")
    private val obdDeviceKey = stringPreferencesKey("obd_device_address")
    private val externalGpsDeviceKey = stringPreferencesKey("external_gps_address")
    private val tpmsEnabledKey = stringPreferencesKey("tpms_enabled_override")
    private val externalGpsEnabledKey = stringPreferencesKey("external_gps_enabled_override")

    val speedUnit: Flow<SpeedUnit> = context.settingsDataStore.data.map { prefs ->
        if (prefs[speedUnitKey] == "imperial") SpeedUnit.IMPERIAL else SpeedUnit.METRIC
    }

    val logIntervalMs: Flow<Long> = context.settingsDataStore.data.map { prefs ->
        prefs[logIntervalKey] ?: dev.foss.expeditiongauge.recording.RecordingWriter.DEFAULT_LOG_INTERVAL_MS
    }

    val obdDeviceAddress: Flow<String?> = context.settingsDataStore.data.map { it[obdDeviceKey] }
    val externalGpsAddress: Flow<String?> = context.settingsDataStore.data.map { it[externalGpsDeviceKey] }

    suspend fun setSpeedUnit(unit: SpeedUnit) {
        context.settingsDataStore.edit { it[speedUnitKey] = if (unit == SpeedUnit.IMPERIAL) "imperial" else "metric" }
    }

    suspend fun setLogIntervalMs(ms: Long) {
        context.settingsDataStore.edit { it[logIntervalKey] = ms }
    }

    suspend fun setObdDeviceAddress(address: String?) {
        context.settingsDataStore.edit {
            if (address == null) it.remove(obdDeviceKey) else it[obdDeviceKey] = address
        }
    }

    suspend fun setExternalGpsAddress(address: String?) {
        context.settingsDataStore.edit {
            if (address == null) it.remove(externalGpsDeviceKey) else it[externalGpsDeviceKey] = address
        }
    }

    suspend fun resetCalibrationFlag() {
        context.settingsDataStore.edit { it[floatPreferencesKey("calibration_reset")] = 1f }
    }
}
