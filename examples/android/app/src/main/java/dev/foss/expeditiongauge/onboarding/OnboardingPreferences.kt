package dev.foss.expeditiongauge.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "onboarding_preferences",
)

private val TOUR_COMPLETED_KEY = booleanPreferencesKey("tour_completed")

class OnboardingPreferences(private val context: Context) {
    val tourCompleted: Flow<Boolean> = context.onboardingDataStore.data.map { prefs ->
        prefs[TOUR_COMPLETED_KEY] ?: false
    }

    suspend fun setTourCompleted(completed: Boolean) {
        context.onboardingDataStore.edit { prefs ->
            prefs[TOUR_COMPLETED_KEY] = completed
        }
    }
}

enum class OnboardingStep {
    Permissions,
    MountLevel,
    FirstRecording,
    LiveSessionTip,
    PlaybackReview,
    OfflineMaps,
    PrivacyBackup,
}
