package dev.foss.expeditiongauge.calibration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    /**
     * Stores raw sensor pitch/roll so the current attitude reads as level (0°, 0°).
     */
    suspend fun setLevel(pitchDeg: Float, rollDeg: Float) {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = pitchDeg
            prefs[rollKey] = rollDeg
        }
    }

    /**
     * Zeros the gauge to the current on-screen attitude. Offsets accumulate on raw sensor
     * values, not on already-calibrated display values (repeated taps stay centered).
     */
    suspend fun zeroToCurrentDisplay(displayPitchDeg: Float, displayRollDeg: Float) {
        val current = offsets.first()
        val next = computeZeroOffsets(displayPitchDeg, displayRollDeg, current)
        setLevel(next.pitchOffsetDeg, next.rollOffsetDeg)
    }

    suspend fun clearOffsets() {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = 0f
            prefs[rollKey] = 0f
        }
    }

    fun applyOffsets(pitchDeg: Float, rollDeg: Float, offsets: CalibrationOffsets): Pair<Float, Float> {
        return Pair(pitchDeg - offsets.pitchOffsetDeg, rollDeg - offsets.rollOffsetDeg)
    }

    companion object {
        /** Maps current display + stored offsets to new raw baseline for a level zero. */
        fun computeZeroOffsets(
            displayPitchDeg: Float,
            displayRollDeg: Float,
            current: CalibrationOffsets,
        ): CalibrationOffsets = CalibrationOffsets(
            pitchOffsetDeg = current.pitchOffsetDeg + displayPitchDeg,
            rollOffsetDeg = current.rollOffsetDeg + displayRollDeg,
        )
    }
}
