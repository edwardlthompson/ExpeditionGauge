package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.alertsnooze.AlertSnooze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertSnoozeStore(private val context: Context) {
    val untilByType: Flow<Map<AlertType, Long>> = context.settingsDataStore.data.map { prefs ->
        AlertSnooze.decode(prefs[KEY])
    }

    suspend fun snooze(type: AlertType, nowMs: Long) {
        update { current -> current + (type to AlertSnooze.untilMs(nowMs)) }
    }

    suspend fun clear(type: AlertType) {
        update { current -> current - type }
    }

    private suspend fun update(transform: (Map<AlertType, Long>) -> Map<AlertType, Long>) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY] = AlertSnooze.encode(transform(AlertSnooze.decode(prefs[KEY])))
        }
    }

    companion object {
        private val KEY = stringPreferencesKey("alert_snooze_until")
    }
}
