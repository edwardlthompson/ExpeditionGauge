package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NightHudStore(private val context: Context) {
    val enabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY] ?: true
    }

    suspend fun setEnabled(value: Boolean) {
        context.settingsDataStore.edit { it[KEY] = value }
    }

    companion object {
        private val KEY = booleanPreferencesKey("night_hud_palette")
    }
}
