package dev.foss.expeditiongauge.settings

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object SettingsPreferencesKeys {
    val speedUnit = stringPreferencesKey("speed_unit")
    val logInterval = longPreferencesKey("log_interval_ms")
    val obdDevice = stringPreferencesKey("obd_device_address")
    val externalGpsDevice = stringPreferencesKey("external_gps_address")
    val tpmsEnabled = booleanPreferencesKey("tpms_enabled")
    val pressureUnit = stringPreferencesKey("pressure_unit")
    val tempUnit = stringPreferencesKey("temp_unit")
    val externalGpsEnabled = booleanPreferencesKey("external_gps_enabled")
    val obdPidRpm = booleanPreferencesKey("obd_pid_rpm")
    val obdPidSpeed = booleanPreferencesKey("obd_pid_speed")
    val obdPidThrottle = booleanPreferencesKey("obd_pid_throttle")
    val obdPidLoad = booleanPreferencesKey("obd_pid_load")
    val obdPidVoltage = booleanPreferencesKey("obd_pid_voltage")
    val obdPidRearWheels = booleanPreferencesKey("obd_pid_rear_wheels")
    val developerMode = booleanPreferencesKey("developer_mode")
    val madgwickBeta = floatPreferencesKey("madgwick_beta")
    val liveTelemetry = booleanPreferencesKey("live_telemetry_enabled")
    val liveSignalWss = stringPreferencesKey("live_signal_wss")
    val calibrationReset = floatPreferencesKey("calibration_reset")

    fun speedUnitFrom(prefs: Preferences): SpeedUnit =
        if (prefs[speedUnit] == "imperial") SpeedUnit.IMPERIAL else SpeedUnit.METRIC

    fun pressureUnitFrom(prefs: Preferences): PressureUnit =
        if (prefs[pressureUnit] == "kpa") PressureUnit.KPA else PressureUnit.PSI

    fun tempUnitFrom(prefs: Preferences): TempUnit =
        if (prefs[tempUnit] == "fahrenheit") TempUnit.FAHRENHEIT else TempUnit.CELSIUS

    fun obdPidConfigFrom(prefs: Preferences): ObdPidConfig = ObdPidConfig(
        rpm = prefs[obdPidRpm] ?: true,
        speed = prefs[obdPidSpeed] ?: true,
        throttle = prefs[obdPidThrottle] ?: true,
        load = prefs[obdPidLoad] ?: true,
        voltage = prefs[obdPidVoltage] ?: true,
        rearWheels = prefs[obdPidRearWheels] ?: true,
    )
}
