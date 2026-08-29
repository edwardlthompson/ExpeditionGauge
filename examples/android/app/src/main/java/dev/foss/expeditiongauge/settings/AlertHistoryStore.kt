package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.alerthistory.AlertHistory
import dev.foss.expeditiongauge.alerthistory.AlertHistoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertHistoryStore(private val context: Context) {
    val entries: Flow<List<AlertHistoryEntry>> = context.settingsDataStore.data.map { prefs ->
        AlertHistory.decode(prefs[KEY])
    }

    suspend fun append(entry: AlertHistoryEntry) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY] = AlertHistory.encode(AlertHistory.append(AlertHistory.decode(prefs[KEY]), entry))
        }
    }

    suspend fun clear() {
        context.settingsDataStore.edit { it.remove(KEY) }
    }

    companion object {
        private val KEY = stringPreferencesKey("alert_history_log")
    }
}
