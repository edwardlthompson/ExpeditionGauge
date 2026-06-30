package dev.foss.expeditiongauge.alerts

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import dev.foss.expeditiongauge.settings.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val UNSET = -1f

class AlertThresholdsPreferences(private val context: Context) {
    private val masterKey = booleanPreferencesKey("alerts_master_enabled")
    private val maxLatGKey = floatPreferencesKey("alerts_max_lat_g")
    private val maxBetaKey = floatPreferencesKey("alerts_max_beta")
    private val maxSlipKey = floatPreferencesKey("alerts_max_slip")
    private val maxPitchKey = floatPreferencesKey("alerts_max_pitch")
    private val maxRollKey = floatPreferencesKey("alerts_max_roll")
    private val maxRpmKey = floatPreferencesKey("alerts_max_rpm")
    private val maxSpeedKey = floatPreferencesKey("alerts_max_speed_mps")
    private val minFuelKey = floatPreferencesKey("alerts_min_fuel_kmpl")
    private val minPressureKey = floatPreferencesKey("alerts_min_pressure_kpa")
    private val maxTempKey = floatPreferencesKey("alerts_max_temp_c")
    private val pressureLossKey = floatPreferencesKey("alerts_pressure_loss_kpa_min")
    private val cooldownKey = longPreferencesKey("alerts_cooldown_ms")

    val thresholds: Flow<AlertThresholds> = context.settingsDataStore.data.map { prefs ->
        AlertThresholds(
            masterEnabled = prefs[masterKey] ?: false,
            maxLatG = prefs[maxLatGKey].fromStored(),
            maxAbsDriftAngleDeg = prefs[maxBetaKey].fromStored(),
            maxSlipRatio = prefs[maxSlipKey].fromStored(),
            maxPitchDeg = prefs[maxPitchKey].fromStored(),
            maxRollDeg = prefs[maxRollKey].fromStored(),
            maxRpm = prefs[maxRpmKey].fromStored(),
            maxSpeedMps = prefs[maxSpeedKey].fromStored(),
            minFuelEconomyKmpl = prefs[minFuelKey].fromStored(),
            minTirePressureKpa = prefs[minPressureKey].fromStored(),
            maxTireTempC = prefs[maxTempKey].fromStored(),
            rapidPressureLossKpaPerMin = prefs[pressureLossKey].fromStored(),
            cooldownMs = prefs[cooldownKey] ?: 3_000L,
        )
    }

    suspend fun setThresholds(thresholds: AlertThresholds) {
        context.settingsDataStore.edit { prefs ->
            prefs[masterKey] = thresholds.masterEnabled
            prefs[maxLatGKey] = thresholds.maxLatG.toStored()
            prefs[maxBetaKey] = thresholds.maxAbsDriftAngleDeg.toStored()
            prefs[maxSlipKey] = thresholds.maxSlipRatio.toStored()
            prefs[maxPitchKey] = thresholds.maxPitchDeg.toStored()
            prefs[maxRollKey] = thresholds.maxRollDeg.toStored()
            prefs[maxRpmKey] = thresholds.maxRpm.toStored()
            prefs[maxSpeedKey] = thresholds.maxSpeedMps.toStored()
            prefs[minFuelKey] = thresholds.minFuelEconomyKmpl.toStored()
            prefs[minPressureKey] = thresholds.minTirePressureKpa.toStored()
            prefs[maxTempKey] = thresholds.maxTireTempC.toStored()
            prefs[pressureLossKey] = thresholds.rapidPressureLossKpaPerMin.toStored()
            prefs[cooldownKey] = thresholds.cooldownMs
        }
    }

    private fun Float?.toStored(): Float = this ?: UNSET

    private fun Float?.fromStored(): Float? = if (this == null || this < 0f) null else this
}
