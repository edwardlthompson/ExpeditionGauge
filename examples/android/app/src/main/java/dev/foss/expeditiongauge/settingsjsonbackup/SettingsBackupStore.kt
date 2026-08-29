package dev.foss.expeditiongauge.settingsjsonbackup

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsBackupStore by preferencesDataStore(name = "settings_backup")

class SettingsBackupStore(private val context: Context) {
    private val blobKey = stringPreferencesKey("last_blob")

    val lastBlob: Flow<String> = context.settingsBackupStore.data.map { it[blobKey].orEmpty() }

    suspend fun save(blob: String) {
        context.settingsBackupStore.edit { it[blobKey] = blob }
    }
}
