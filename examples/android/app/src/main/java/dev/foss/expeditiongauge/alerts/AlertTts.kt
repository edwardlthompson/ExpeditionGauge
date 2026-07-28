package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/** Locale-aware spoken alert phrases — routes as navigation guidance for AA/DHU. */
class AlertTts(context: Context) {
    private val ready = AtomicBoolean(false)
    private var engine: TextToSpeech? = null
    private val focus = AlertAudioFocus(context.applicationContext, AlertAudioFocus.NAV_GUIDANCE)
    private val activeUtteranceId = AtomicReference<String?>(null)

    init {
        val app = context.applicationContext
        engine = TextToSpeech(app) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.getDefault()
                engine?.setAudioAttributes(AlertAudioFocus.NAV_GUIDANCE)
                engine?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) = Unit

                        override fun onDone(utteranceId: String?) = endUtterance(utteranceId)

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) = endUtterance(utteranceId)

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            Log.w(TAG, "TTS error utterance=$utteranceId code=$errorCode")
                            endUtterance(utteranceId)
                        }

                        override fun onStop(utteranceId: String?, interrupted: Boolean) {
                            endUtterance(utteranceId)
                        }
                    },
                )
                ready.set(true)
                Log.d(TAG, "TTS ready")
            } else {
                Log.w(TAG, "TTS init failed status=$status")
            }
        }
    }

    fun speak(phrase: String) {
        if (!ready.get() || phrase.isBlank()) {
            Log.w(TAG, "speak skipped ready=${ready.get()} blank=${phrase.isBlank()}")
            return
        }
        if (!focus.request()) {
            Log.w(TAG, "audio focus denied; speak may be muted by AudioHardening")
        }
        val utteranceId = "alert_${System.nanoTime()}"
        activeUtteranceId.set(utteranceId)
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        val result = engine?.speak(phrase, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        Log.d(TAG, "speak result=$result phrase=$phrase")
        if (result != TextToSpeech.SUCCESS) {
            endUtterance(utteranceId)
        }
    }

    fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
        ready.set(false)
        activeUtteranceId.set(null)
        focus.abandon()
    }

    private fun endUtterance(utteranceId: String?) {
        if (utteranceId != null && activeUtteranceId.compareAndSet(utteranceId, null)) {
            focus.abandon()
        }
    }

    private companion object {
        const val TAG = "ExpeditionGauge/AlertTts"
    }
}
