package dev.foss.expeditiongauge.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.brightnessDataStore: DataStore<Preferences> by preferencesDataStore(name = "brightness_preferences")

private val BRIGHTNESS_MODE_KEY = stringPreferencesKey("brightness_mode")

class BrightnessPreferences(private val context: Context) {
    val brightnessMode: Flow<BrightnessMode> = context.brightnessDataStore.data.map { prefs ->
        when (prefs[BRIGHTNESS_MODE_KEY]) {
            BrightnessMode.Day.name -> BrightnessMode.Day
            BrightnessMode.Night.name -> BrightnessMode.Night
            else -> BrightnessMode.Auto
        }
    }

    suspend fun setBrightnessMode(mode: BrightnessMode) {
        context.brightnessDataStore.edit { prefs ->
            prefs[BRIGHTNESS_MODE_KEY] = mode.name
        }
    }
}
