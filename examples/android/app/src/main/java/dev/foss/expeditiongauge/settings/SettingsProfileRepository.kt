package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.expeditiongauge.data.db.dao.SettingsProfileDao
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

private val Context.settingsProfileDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings_profile_preferences",
)

private val ACTIVE_PROFILE_ID_KEY = longPreferencesKey("active_profile_id")

class SettingsProfileRepository(
    private val context: Context,
    private val dao: SettingsProfileDao,
) {
    val activeProfileId: Flow<Long> = context.settingsProfileDataStore.data.map { prefs ->
        prefs[ACTIVE_PROFILE_ID_KEY] ?: 1L
    }

    val activeProfile: Flow<SettingsProfile> = activeProfileId.flatMapLatest { id ->
        dao.observeAll().map { entities ->
            entities.firstOrNull { it.id == id }?.let(SettingsProfile::fromEntity)
                ?: SettingsProfile.defaultProfile()
        }
    }

    suspend fun ensureDefaultProfile() {
        if (dao.count() == 0) {
            dao.insert(SettingsProfile.defaultProfile().toEntity())
        }
    }

    suspend fun updatePresetForActiveProfile(presetId: DashboardPresetId) {
        val preset = DashboardPreset.fromId(presetId)
        val profileId = activeProfileId.first()
        val entity = dao.getById(profileId) ?: SettingsProfile.defaultProfile().toEntity()
        val profile = SettingsProfile.fromEntity(entity).copy(
            presetId = presetId,
            recordingMode = preset.recordingMode,
        )
        dao.update(profile.toEntity())
    }

    suspend fun updateRecordingMode(mode: RecordingMode) {
        val profileId = activeProfileId.first()
        val entity = dao.getById(profileId) ?: SettingsProfile.defaultProfile().toEntity()
        val profile = SettingsProfile.fromEntity(entity).copy(recordingMode = mode)
        dao.update(profile.toEntity())
    }

    suspend fun updatePlaybackLayout(mapWeight: Float, graphsExpanded: Boolean) {
        val profileId = activeProfileId.first()
        val entity = dao.getById(profileId) ?: SettingsProfile.defaultProfile().toEntity()
        val profile = SettingsProfile.fromEntity(entity).copy(
            playbackMapWeight = mapWeight.coerceIn(0.2f, 0.8f),
            playbackGraphsExpanded = graphsExpanded,
        )
        dao.update(profile.toEntity())
    }
}
