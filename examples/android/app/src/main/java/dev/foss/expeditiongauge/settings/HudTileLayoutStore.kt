package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.hudtile.HudTileId
import dev.foss.expeditiongauge.hudtile.HudTileLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HudTileLayoutStore(private val context: Context) {
    val order: Flow<List<HudTileId>> = context.settingsDataStore.data.map { prefs ->
        HudTileLayout.parse(prefs[KEY])
    }

    suspend fun set(ids: List<HudTileId>) {
        context.settingsDataStore.edit { it[KEY] = HudTileLayout.encode(ids) }
    }

    suspend fun cycle() {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY] = HudTileLayout.encode(HudTileLayout.cycle(HudTileLayout.parse(prefs[KEY])))
        }
    }

    companion object {
        private val KEY = stringPreferencesKey("hud_tile_order")
    }
}
