package dev.foss.expeditiongauge.car.aainclineaudio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import dev.foss.expeditiongauge.alerts.AlertType
import kotlin.math.sin

/** Pitch/roll alert beeps ride the AA nav-guidance route while a car session is live. */
object AaInclinometerAudio {
    const val TONE_MS = 150
    const val SAMPLE_RATE = 16_000

    fun useNavRoute(aaSessionLive: Boolean, type: AlertType): Boolean =
        aaSessionLive && (type == AlertType.PITCH || type == AlertType.ROLL)

    fun playBeep(type: AlertType) {
        val hz = if (type == AlertType.PITCH) PITCH_HZ else ROLL_HZ
        playTone(hz, TONE_MS)
    }

    fun sampleCount(sampleRate: Int, durationMs: Int): Int =
        (sampleRate * durationMs / 1000).coerceAtLeast(1)

    private fun playTone(hz: Int, durationMs: Int) {
        val n = sampleCount(SAMPLE_RATE, durationMs)
        val buf = ShortArray(n)
        val step = 2.0 * Math.PI * hz / SAMPLE_RATE
        val attack = (n / 10).coerceAtLeast(1)
        for (i in 0 until n) {
            val env = when {
                i < attack -> i.toDouble() / attack
                i > n - attack -> (n - i).toDouble() / attack
                else -> 1.0
            }
            buf[i] = (sin(i * step) * 0.35 * env * Short.MAX_VALUE).toInt().toShort()
        }
        val track = AudioTrack.Builder()
            .setAudioAttributes(navAttrs())
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(n * 2)
            .build()
        track.write(buf, 0, n)
        track.play()
        Handler(Looper.getMainLooper()).postDelayed({
            runCatching {
                track.stop()
                track.release()
            }
        }, (durationMs + 40).toLong())
    }

    fun navAttrs(): AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private const val PITCH_HZ = 880
    private const val ROLL_HZ = 660
}
