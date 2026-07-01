package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.recording.SessionStorageBudget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

enum class SpeedUnit { METRIC, IMPERIAL }

class SettingsPreferences(private val context: Context) {
    private val trackGauge = TrackGaugePreferences(context)
    private val keys = SettingsPreferencesKeys
    private val store = SettingsPreferencesStore(context, trackGauge)

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

    val androidAutoEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[keys.androidAutoEnabled] ?: false
    }

    val androidAutoMetricAllowlist: Flow<Set<String>> = context.settingsDataStore.data.map { prefs ->
        val raw = prefs[keys.androidAutoMetrics]
        if (raw.isNullOrBlank()) {
            dev.foss.expeditiongauge.car.AndroidAutoBridge.DEFAULT_ALLOWLIST
        } else {
            raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        }
    }

    val mediaCompressionQuality: Flow<MediaCompressionQuality> = context.settingsDataStore.data.map { prefs ->
        val raw = prefs[keys.mediaCompression] ?: MediaCompressionQuality.BALANCED.name
        runCatching { MediaCompressionQuality.valueOf(raw) }.getOrDefault(MediaCompressionQuality.BALANCED)
    }

    val autoRecordEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[keys.autoRecordEnabled] ?: false
    }

    val autoRecordDeviceAddresses: Flow<Set<String>> = context.settingsDataStore.data.map { prefs ->
        prefs[keys.autoRecordDevices]
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()
    }

    val sessionStorageFreePercent: Flow<Int> = context.settingsDataStore.data.map {
        (it[keys.sessionStorageFreePercent] ?: SessionStorageBudget.DEFAULT_PERCENT)
            .coerceIn(SessionStorageBudget.MIN_PERCENT, SessionStorageBudget.MAX_PERCENT)
    }

    suspend fun setSpeedUnit(unit: SpeedUnit) = store.setSpeedUnit(unit)
    suspend fun setLogIntervalMs(ms: Long) = store.setLogIntervalMs(ms)
    suspend fun setObdDeviceAddress(address: String?) = store.setObdDeviceAddress(address)
    suspend fun setExternalGpsAddress(address: String?) = store.setExternalGpsAddress(address)
    suspend fun setExternalGpsEnabled(enabled: Boolean) = store.setExternalGpsEnabled(enabled)
    suspend fun forgetExternalGpsDevice() = store.forgetExternalGpsDevice()
    suspend fun setTpmsEnabled(enabled: Boolean) = store.setTpmsEnabled(enabled)
    suspend fun setPressureUnit(unit: PressureUnit) = store.setPressureUnit(unit)
    suspend fun setTempUnit(unit: TempUnit) = store.setTempUnit(unit)
    suspend fun setObdPidConfig(config: ObdPidConfig) = store.setObdPidConfig(config)
    suspend fun setLapTimingEnabled(enabled: Boolean) = store.setLapTimingEnabled(enabled)
    suspend fun setTrackStartFinishGeoJson(geoJson: String?) = store.setTrackStartFinishGeoJson(geoJson)
    suspend fun setTrackSectorLinesGeoJson(geoJson: String?) = store.setTrackSectorLinesGeoJson(geoJson)
    suspend fun setAttitudeGaugeMode(mode: AttitudeGaugeMode) = store.setAttitudeGaugeMode(mode)
    suspend fun clearTrackConfig() = store.clearTrackConfig()
    suspend fun setDeveloperModeEnabled(enabled: Boolean) = store.setDeveloperModeEnabled(enabled)
    suspend fun setMadgwickBeta(beta: Float) = store.setMadgwickBeta(beta)
    suspend fun setLiveTelemetryEnabled(enabled: Boolean) = store.setLiveTelemetryEnabled(enabled)
    suspend fun setLiveSignalWssUrl(url: String) = store.setLiveSignalWssUrl(url)
    suspend fun setAndroidAutoEnabled(enabled: Boolean) = store.setAndroidAutoEnabled(enabled)
    suspend fun setAndroidAutoMetricAllowlist(allowlist: Set<String>) = store.setAndroidAutoMetricAllowlist(allowlist)
    suspend fun toggleAndroidAutoMetric(key: String) = store.toggleAndroidAutoMetric(key)
    suspend fun setMediaCompressionQuality(quality: MediaCompressionQuality) = store.setMediaCompressionQuality(quality)
    suspend fun setAutoRecordEnabled(enabled: Boolean) = store.setAutoRecordEnabled(enabled)
    suspend fun setAutoRecordDeviceAddresses(addresses: Set<String>) = store.setAutoRecordDeviceAddresses(addresses)
    suspend fun setSessionStorageFreePercent(percent: Int) = store.setSessionStorageFreePercent(percent)
    suspend fun resetCalibrationFlag() = store.resetCalibrationFlag()
}
