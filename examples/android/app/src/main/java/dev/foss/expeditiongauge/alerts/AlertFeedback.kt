package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic + audible chime feedback for live alerts.
 */
class AlertFeedback(@Suppress("UNUSED_PARAMETER") context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)

    @Suppress("UNUSED_PARAMETER")
    fun onAlert(type: AlertType) {
        vibrate(type)
        val tone = when (type) {
            AlertType.LAT_G, AlertType.DRIFT_ANGLE -> ToneGenerator.TONE_PROP_BEEP
            AlertType.RPM, AlertType.SPEED -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            else -> ToneGenerator.TONE_PROP_ACK
        }
        toneGenerator.startTone(tone, 150)
    }

    private fun vibrate(type: AlertType) {
        val duration = when (type) {
            AlertType.LAT_G, AlertType.DRIFT_ANGLE -> 120L
            AlertType.RPM, AlertType.SPEED -> 80L
            else -> 60L
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(duration)
        }
    }

    fun release() {
        toneGenerator.release()
    }
}
