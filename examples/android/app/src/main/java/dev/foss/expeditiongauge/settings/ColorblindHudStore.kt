package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.colorblind.ColorblindHud
import dev.foss.expeditiongauge.colorblind.ColorblindHudMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ColorblindHudStore(private val context: Context) {
    val mode: Flow<ColorblindHudMode> = context.settingsDataStore.data.map { prefs ->
        ColorblindHud.parse(prefs[KEY])
    }

    suspend fun cycle() {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY] = ColorblindHud.cycle(ColorblindHud.parse(prefs[KEY])).name
        }
    }

    companion object {
        private val KEY = stringPreferencesKey("colorblind_hud_mode")
    }
}
