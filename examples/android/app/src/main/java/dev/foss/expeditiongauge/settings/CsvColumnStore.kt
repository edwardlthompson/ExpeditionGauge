package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.foss.expeditiongauge.csvcolumns.CsvColumnPicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CsvColumnStore(private val context: Context) {
    val columns: Flow<Set<String>> = context.settingsDataStore.data.map { prefs ->
        CsvColumnPicker.parse(prefs[KEY]).ifEmpty { CsvColumnPicker.ALL.toSet() }
    }

    suspend fun toggle(column: String) {
        context.settingsDataStore.edit { prefs ->
            val current = CsvColumnPicker.parse(prefs[KEY]).ifEmpty { CsvColumnPicker.ALL.toSet() }
            prefs[KEY] = CsvColumnPicker.encode(CsvColumnPicker.toggle(current, column))
        }
    }

    companion object {
        private val KEY = stringPreferencesKey("csv_export_columns")
    }
}
