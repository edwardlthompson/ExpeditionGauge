package dev.foss.expeditiongauge.calibration

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.calibrationWizardDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "calibration_wizard",
)

class CalibrationWizardStore(private val context: Context) {
    private val completedKey = booleanPreferencesKey("wizard_completed")

    val wizardCompleted: Flow<Boolean> = context.calibrationWizardDataStore.data.map {
        it[completedKey] ?: false
    }

    suspend fun markCompleted() {
        context.calibrationWizardDataStore.edit { it[completedKey] = true }
    }

    suspend fun reset() {
        context.calibrationWizardDataStore.edit { it.remove(completedKey) }
    }
}

enum class CalibrationWizardStep {
    Mount,
    Level,
    ImuCorners,
    Figure8,
    TestDrive,
}
