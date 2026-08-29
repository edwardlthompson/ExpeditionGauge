package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.aainclineaudio.AaInclinometerAudio
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic + audible chime feedback for live alerts.
 * Beeps use [AudioManager.STREAM_MUSIC] with transient media focus (AA-friendly).
 */
class AlertFeedback(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val focus = AlertAudioFocus(context.applicationContext, AlertAudioFocus.MEDIA)
    private val navFocus = AlertAudioFocus(context.applicationContext, AlertAudioFocus.NAV_GUIDANCE)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    fun onAlert(type: AlertType, playTone: Boolean = true, haptic: Boolean = true) {
        if (haptic) vibrate(type)
        if (!playTone) return
        if (AaInclinometerAudio.useNavRoute(CarAppBridgeRegistry.sessionLive, type)) {
            navFocus.request()
            AaInclinometerAudio.playBeep(type)
            mainHandler.removeCallbacks(abandonNavFocusRunnable)
            mainHandler.postDelayed(abandonNavFocusRunnable, (AaInclinometerAudio.TONE_MS + 50).toLong())
            return
        }
        focus.request()
        val tone = when (type) {
            AlertType.LAT_G, AlertType.DRIFT_ANGLE -> ToneGenerator.TONE_PROP_BEEP
            AlertType.PITCH -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            AlertType.ROLL -> ToneGenerator.TONE_PROP_ACK
            AlertType.RPM, AlertType.SPEED -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            else -> ToneGenerator.TONE_PROP_ACK
        }
        toneGenerator.startTone(tone, TONE_MS)
        mainHandler.removeCallbacks(abandonFocusRunnable)
        mainHandler.postDelayed(abandonFocusRunnable, (TONE_MS + 50).toLong())
    }

    private val abandonFocusRunnable = Runnable { focus.abandon() }
    private val abandonNavFocusRunnable = Runnable { navFocus.abandon() }

    private fun vibrate(type: AlertType) {
        val duration = when (type) {
            AlertType.LAT_G, AlertType.DRIFT_ANGLE -> 120L
            AlertType.RPM, AlertType.SPEED -> 80L
            else -> 60L
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        } catch (_: SecurityException) {
            // VIBRATE permission may be denied on some installs.
        }
    }

    fun release() {
        mainHandler.removeCallbacks(abandonFocusRunnable)
        mainHandler.removeCallbacks(abandonNavFocusRunnable)
        focus.abandon()
        navFocus.abandon()
        toneGenerator.release()
    }

    private companion object {
        const val TONE_MS = 150
    }
}
