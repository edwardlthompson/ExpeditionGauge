package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.sessionfavorites.SessionFavorites
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionFavoritesStore(private val context: Context) {
    val favoriteIds: Flow<Set<Long>> = context.settingsDataStore.data.map { prefs ->
        SessionFavorites.parseIds(prefs[KEY])
    }

    suspend fun toggle(sessionId: Long) {
        context.settingsDataStore.edit { prefs ->
            val next = SessionFavorites.toggle(SessionFavorites.parseIds(prefs[KEY]), sessionId)
            if (next.isEmpty()) prefs.remove(KEY) else prefs[KEY] = SessionFavorites.encodeIds(next)
        }
    }

    suspend fun isFavorite(sessionId: Long): Boolean = sessionId in favoriteIds.first()

    companion object {
        private val KEY = stringPreferencesKey("session_favorite_ids")
    }
}
