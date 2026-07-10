package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.car.gauge.inclinometerStyleFromStorage
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TrackGaugePreferences(private val context: Context) {
    private val lapTimingEnabledKey = booleanPreferencesKey("lap_timing_enabled")
    private val trackStartFinishKey = stringPreferencesKey("track_start_finish_geojson")
    private val trackSectorsKey = stringPreferencesKey("track_sector_lines_geojson")
    private val attitudeGaugeModeKey = stringPreferencesKey("attitude_gauge_mode")
    private val inclinometerStyleKey = stringPreferencesKey("inclinometer_style")

    val lapTimingEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[lapTimingEnabledKey] ?: false
    }

    val trackStartFinishGeoJson: Flow<String?> = context.settingsDataStore.data.map {
        it[trackStartFinishKey]
    }

    val trackSectorLinesGeoJson: Flow<String?> = context.settingsDataStore.data.map {
        it[trackSectorsKey]
    }

    val attitudeGaugeMode: Flow<AttitudeGaugeMode> = context.settingsDataStore.data.map { prefs ->
        when (prefs[attitudeGaugeModeKey]) {
            "g_force" -> AttitudeGaugeMode.G_FORCE
            "hybrid" -> AttitudeGaugeMode.HYBRID
            "inclinometer" -> AttitudeGaugeMode.INCLINOMETER
            else -> AttitudeGaugeMode.ATTITUDE
        }
    }

    val inclinometerStyle: Flow<InclinometerStyle> = context.settingsDataStore.data.map { prefs ->
        inclinometerStyleFromStorage(prefs[inclinometerStyleKey])
    }

    suspend fun setLapTimingEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[lapTimingEnabledKey] = enabled }
    }

    suspend fun setTrackStartFinishGeoJson(geoJson: String?) {
        context.settingsDataStore.edit {
            if (geoJson == null) it.remove(trackStartFinishKey) else it[trackStartFinishKey] = geoJson
        }
    }

    suspend fun setTrackSectorLinesGeoJson(geoJson: String?) {
        context.settingsDataStore.edit {
            if (geoJson == null) it.remove(trackSectorsKey) else it[trackSectorsKey] = geoJson
        }
    }

    suspend fun setAttitudeGaugeMode(mode: AttitudeGaugeMode) {
        context.settingsDataStore.edit {
            it[attitudeGaugeModeKey] = when (mode) {
                AttitudeGaugeMode.G_FORCE -> "g_force"
                AttitudeGaugeMode.HYBRID -> "hybrid"
                AttitudeGaugeMode.INCLINOMETER -> "inclinometer"
                AttitudeGaugeMode.ATTITUDE -> "attitude"
            }
        }
    }

    suspend fun setInclinometerStyle(style: InclinometerStyle) {
        context.settingsDataStore.edit {
            it[inclinometerStyleKey] = style.name.lowercase()
        }
    }

    suspend fun clearTrackConfig() {
        context.settingsDataStore.edit {
            it.remove(trackStartFinishKey)
            it.remove(trackSectorsKey)
        }
    }
}
