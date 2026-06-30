package dev.foss.expeditiongauge.calibration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.calibrationDataStore: DataStore<Preferences> by preferencesDataStore(name = "calibration_store")

data class CalibrationOffsets(
    val pitchOffsetDeg: Float = 0f,
    val rollOffsetDeg: Float = 0f,
)

class CalibrationStore(private val context: Context) {
    private val pitchKey = floatPreferencesKey("pitch_offset_deg")
    private val rollKey = floatPreferencesKey("roll_offset_deg")

    val offsets: Flow<CalibrationOffsets> = context.calibrationDataStore.data.map { prefs ->
        CalibrationOffsets(
            pitchOffsetDeg = prefs[pitchKey] ?: 0f,
            rollOffsetDeg = prefs[rollKey] ?: 0f,
        )
    }

    suspend fun setLevel(pitchDeg: Float, rollDeg: Float) {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = pitchDeg
            prefs[rollKey] = rollDeg
        }
    }

    fun applyOffsets(pitchDeg: Float, rollDeg: Float, offsets: CalibrationOffsets): Pair<Float, Float> {
        return Pair(pitchDeg - offsets.pitchOffsetDeg, rollDeg - offsets.rollOffsetDeg)
    }
}
