package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.drivingModeDataStore: DataStore<Preferences> by preferencesDataStore(name = "driving_mode_preferences")

private val DRIVING_MODE_ENABLED = booleanPreferencesKey("driving_mode_enabled")
private val LOCK_LANDSCAPE_RECORDING = booleanPreferencesKey("lock_landscape_while_recording")

class DrivingModePreferences(private val context: Context) {
    val drivingModeEnabled: Flow<Boolean> = context.drivingModeDataStore.data.map { prefs ->
        prefs[DRIVING_MODE_ENABLED] ?: false
    }

    val lockLandscapeWhileRecording: Flow<Boolean> = context.drivingModeDataStore.data.map { prefs ->
        prefs[LOCK_LANDSCAPE_RECORDING] ?: true
    }

    suspend fun setDrivingModeEnabled(enabled: Boolean) {
        context.drivingModeDataStore.edit { prefs ->
            prefs[DRIVING_MODE_ENABLED] = enabled
        }
    }

    suspend fun setLockLandscapeWhileRecording(enabled: Boolean) {
        context.drivingModeDataStore.edit { prefs ->
            prefs[LOCK_LANDSCAPE_RECORDING] = enabled
        }
    }
}
