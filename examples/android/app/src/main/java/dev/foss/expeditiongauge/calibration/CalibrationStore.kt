package dev.foss.expeditiongauge.calibration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.math.abs

private val Context.calibrationDataStore: DataStore<Preferences> by preferencesDataStore(name = "calibration_store")

data class CalibrationOffsets(
    val pitchOffsetDeg: Float = 0f,
    val rollOffsetDeg: Float = 0f,
    val yawOffsetDeg: Float = 0f,
    /** Surface.ROTATION_* when Zero was last applied. */
    val displayRotationAtCalibrate: Int = 0,
    /** Epoch ms of last successful zero (survives process death). */
    val lastZeroAtMs: Long = 0L,
) {
    fun hasPersistedZero(): Boolean =
        lastZeroAtMs > 0L ||
            abs(pitchOffsetDeg) > 0.01f ||
            abs(rollOffsetDeg) > 0.01f ||
            abs(yawOffsetDeg) > 0.01f
}

class CalibrationStore(private val context: Context) {
    private val pitchKey = floatPreferencesKey("pitch_offset_deg")
    private val rollKey = floatPreferencesKey("roll_offset_deg")
    private val yawKey = floatPreferencesKey("yaw_offset_deg")
    private val rotationKey = intPreferencesKey("display_rotation_at_calibrate")
    private val lastZeroAtKey = longPreferencesKey("last_zero_at_ms")

    val offsets: Flow<CalibrationOffsets> = context.calibrationDataStore.data.map { prefs ->
        CalibrationOffsets(
            pitchOffsetDeg = prefs[pitchKey] ?: 0f,
            rollOffsetDeg = prefs[rollKey] ?: 0f,
            yawOffsetDeg = prefs[yawKey] ?: 0f,
            displayRotationAtCalibrate = prefs[rotationKey] ?: 0,
            lastZeroAtMs = prefs[lastZeroAtKey] ?: 0L,
        )
    }

    /** Blocking-friendly first read for sensor bootstrap before IMU listeners attach. */
    suspend fun currentOffsets(): CalibrationOffsets = offsets.first()

    suspend fun setLevel(
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float = 0f,
        displayRotation: Int = 0,
    ) {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = pitchDeg
            prefs[rollKey] = rollDeg
            prefs[yawKey] = yawDeg
            prefs[rotationKey] = displayRotation.mod(4)
            prefs[lastZeroAtKey] = System.currentTimeMillis()
        }
    }

    /** One Zero for all orientations — stores [displayRotation] for relative remap. */
    suspend fun zeroToCurrentDisplay(
        displayPitchDeg: Float,
        displayRollDeg: Float,
        displayYawDeg: Float = 0f,
        displayRotation: Int = 0,
        includeYaw: Boolean = true,
    ) {
        val current = offsets.first()
        val next = computeZeroOffsets(
            displayPitchDeg, displayRollDeg, displayYawDeg, current, includeYaw,
        )
        setLevel(
            next.pitchOffsetDeg,
            next.rollOffsetDeg,
            next.yawOffsetDeg,
            displayRotation,
        )
    }

    suspend fun clearOffsets() {
        context.calibrationDataStore.edit { prefs ->
            prefs[pitchKey] = 0f
            prefs[rollKey] = 0f
            prefs[yawKey] = 0f
            prefs[rotationKey] = 0
            prefs[lastZeroAtKey] = 0L
        }
    }

    fun applyOffsets(
        pitchDeg: Float,
        rollDeg: Float,
        offsets: CalibrationOffsets,
    ): Pair<Float, Float> = Pair(
        pitchDeg - offsets.pitchOffsetDeg,
        rollDeg - offsets.rollOffsetDeg,
    )

    fun applyYaw(yawDeg: Float, offsets: CalibrationOffsets): Float =
        wrapSigned180(yawDeg - offsets.yawOffsetDeg)

    fun applyAttitude(
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
        offsets: CalibrationOffsets,
    ): Triple<Float, Float, Float> {
        val (p, r) = applyOffsets(pitchDeg, rollDeg, offsets)
        return Triple(p, r, applyYaw(yawDeg, offsets))
    }

    companion object {
        fun computeZeroOffsets(
            displayPitchDeg: Float,
            displayRollDeg: Float,
            displayYawDeg: Float = 0f,
            current: CalibrationOffsets,
            includeYaw: Boolean = true,
        ): CalibrationOffsets = CalibrationOffsets(
            pitchOffsetDeg = current.pitchOffsetDeg + displayPitchDeg,
            rollOffsetDeg = current.rollOffsetDeg + displayRollDeg,
            yawOffsetDeg = if (includeYaw) {
                wrapSigned180(current.yawOffsetDeg + displayYawDeg)
            } else {
                current.yawOffsetDeg
            },
            displayRotationAtCalibrate = current.displayRotationAtCalibrate,
            lastZeroAtMs = current.lastZeroAtMs,
        )

        fun wrapSigned180(deg: Float): Float {
            var d = deg % 360f
            if (d > 180f) d -= 360f
            if (d <= -180f) d += 360f
            return d
        }

        fun alreadyLevel(pitchDeg: Float, rollDeg: Float, thresholdDeg: Float = 2f): Boolean =
            abs(pitchDeg) < thresholdDeg && abs(rollDeg) < thresholdDeg
    }
}
