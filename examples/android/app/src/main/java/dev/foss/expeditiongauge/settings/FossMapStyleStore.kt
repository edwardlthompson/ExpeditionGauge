package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FossMapStyleStore(private val context: Context) {
    val styleId: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY] ?: "demo"
    }

    suspend fun setStyleId(id: String) {
        context.settingsDataStore.edit { it[KEY] = id }
    }

    companion object {
        private val KEY = stringPreferencesKey("foss_map_style")
    }
}
