package dev.foss.expeditiongauge.inappwhatsnew

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.whatsNewStore by preferencesDataStore(name = "whats_new")

class WhatsNewStore(private val context: Context) {
    private val seenKey = stringPreferencesKey("seen_version")

    val seenVersion: Flow<String?> = context.whatsNewStore.data.map { it[seenKey] }

    suspend fun markSeen(version: String = WhatsNew.CURRENT) {
        context.whatsNewStore.edit { it[seenKey] = version }
    }
}
