package dev.foss.expeditiongauge.accessibility

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.accessibilityDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "accessibility_preferences",
)

private val HIGH_CONTRAST_KEY = booleanPreferencesKey("high_contrast")
private val AUDIBLE_TONES_KEY = booleanPreferencesKey("audible_tones")
private val LARGE_TEXT_KEY = booleanPreferencesKey("large_text")
private val TTS_READOUT_KEY = booleanPreferencesKey("tts_readout")

class AccessibilityPreferences(private val context: Context) {
    val highContrastEnabled: Flow<Boolean> = context.accessibilityDataStore.data.map { prefs ->
        prefs[HIGH_CONTRAST_KEY] ?: false
    }

    val audibleTonesEnabled: Flow<Boolean> = context.accessibilityDataStore.data.map { prefs ->
        prefs[AUDIBLE_TONES_KEY] ?: false
    }

    val largeTextEnabled: Flow<Boolean> = context.accessibilityDataStore.data.map { prefs ->
        prefs[LARGE_TEXT_KEY] ?: false
    }

    val ttsReadoutEnabled: Flow<Boolean> = context.accessibilityDataStore.data.map { prefs ->
        prefs[TTS_READOUT_KEY] ?: false
    }

    suspend fun setHighContrastEnabled(enabled: Boolean) {
        context.accessibilityDataStore.edit { prefs ->
            prefs[HIGH_CONTRAST_KEY] = enabled
        }
    }

    suspend fun setAudibleTonesEnabled(enabled: Boolean) {
        context.accessibilityDataStore.edit { prefs ->
            prefs[AUDIBLE_TONES_KEY] = enabled
        }
    }

    suspend fun setLargeTextEnabled(enabled: Boolean) {
        context.accessibilityDataStore.edit { prefs ->
            prefs[LARGE_TEXT_KEY] = enabled
        }
    }

    suspend fun setTtsReadoutEnabled(enabled: Boolean) {
        context.accessibilityDataStore.edit { prefs ->
            prefs[TTS_READOUT_KEY] = enabled
        }
    }
}

class AudibleTones(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null

    fun playMarkEventTone(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            toneGenerator?.release()
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
