package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log

/**
 * Transient audio focus for alert beep/TTS so OEM AudioHardening and AA/DHU
 * do not mute playback as background (common for [com.google.android.tts]).
 */
class AlertAudioFocus(
    context: Context,
    private val attrs: AudioAttributes,
) {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private var focusRequest: AudioFocusRequest? = null
    private val focusLock = Any()

    fun request(): Boolean {
        val am = audioManager ?: return false
        synchronized(focusLock) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = focusRequest ?: AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
                )
                    .setAudioAttributes(attrs)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener { }
                    .build()
                    .also { focusRequest = it }
                val result = am.requestAudioFocus(req)
                Log.d(TAG, "requestAudioFocus result=$result")
                return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
            @Suppress("DEPRECATION")
            val result = am.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
            )
            Log.d(TAG, "requestAudioFocus(legacy) result=$result")
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    fun abandon() {
        val am = audioManager ?: return
        synchronized(focusLock) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusRequest?.let { am.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus(null)
            }
        }
    }

    companion object {
        private const val TAG = "ExpeditionGauge/AlertTts"

        val NAV_GUIDANCE: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val MEDIA: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }
}
