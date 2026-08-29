package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.dualdashcam.DashcamClip
import dev.foss.expeditiongauge.dualdashcam.DualDashcam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DualDashcamStore(private val context: Context) {
    fun clips(sessionId: Long): Flow<List<DashcamClip>> = context.settingsDataStore.data.map { prefs ->
        DualDashcam.parse(prefs[key(sessionId)])
    }

    suspend fun add(sessionId: Long, uri: String, offsetMs: Long = 0L) {
        context.settingsDataStore.edit { prefs ->
            prefs[key(sessionId)] = DualDashcam.plus(prefs[key(sessionId)], uri, offsetMs)
        }
    }

    companion object {
        private fun key(sessionId: Long) = stringPreferencesKey("dashcam_extra_$sessionId")
    }
}
