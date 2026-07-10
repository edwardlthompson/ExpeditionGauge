package dev.foss.expeditiongauge.calibration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.calibrationDataStore: DataStore<Preferences> by preferencesDataStore(name = "calibration_store")

data class CalibrationOffsets(
    val pitchOffsetDeg: Float = 0f,
    val rollOffsetDeg: Float = 0f,
    /** Surface.ROTATION_* when Zero was last applied. */
    val displayRotationAtCalibrate: Int = 0,
)

class CalibrationStore(private val context: Context) {
    private val pitchKey = floatPreferencesKey("pitch_offset_deg")
    private val rollKey = floatPreferencesKey("roll_offset_deg")
    private val rotationKey = intPreferencesKey("display_rotation_at_calibrate")

    val offsets: Flow<CalibrationOffsets> = context.calibrationDataStore.data.map { prefs ->
        CalibrationOffsets(
            pitchOffsetDeg = prefs[pitchKey] ?: 0f,
            rollOffsetDeg = prefs[rollKey] ?: 0f,
            displayRotationAtCalibrate = prefs[rotationKey] ?: 0,
        )
    }

    suspend fun setLevel(pitchDeg: Float, rollDeg: Float, displayRotation: Int = 0) {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = pitchDeg
            prefs[rollKey] = rollDeg
            prefs[rotationKey] = displayRotation.mod(4)
        }
    }

    /** One Zero for all orientations — stores [displayRotation] for relative remap. */
    suspend fun zeroToCurrentDisplay(
        displayPitchDeg: Float,
        displayRollDeg: Float,
        displayRotation: Int = 0,
    ) {
        val current = offsets.first()
        val next = computeZeroOffsets(displayPitchDeg, displayRollDeg, current)
        setLevel(next.pitchOffsetDeg, next.rollOffsetDeg, displayRotation)
    }

    suspend fun clearOffsets() {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = 0f
            prefs[rollKey] = 0f
            prefs[rotationKey] = 0
        }
    }

    fun applyOffsets(pitchDeg: Float, rollDeg: Float, offsets: CalibrationOffsets): Pair<Float, Float> =
        Pair(pitchDeg - offsets.pitchOffsetDeg, rollDeg - offsets.rollOffsetDeg)

    companion object {
        fun computeZeroOffsets(
            displayPitchDeg: Float,
            displayRollDeg: Float,
            current: CalibrationOffsets,
        ): CalibrationOffsets = CalibrationOffsets(
            pitchOffsetDeg = current.pitchOffsetDeg + displayPitchDeg,
            rollOffsetDeg = current.rollOffsetDeg + displayRollDeg,
            displayRotationAtCalibrate = current.displayRotationAtCalibrate,
        )
    }
}
