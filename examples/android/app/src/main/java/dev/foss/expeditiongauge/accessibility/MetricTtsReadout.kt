package dev.foss.expeditiongauge.accessibility

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import java.util.Locale
import kotlin.math.abs

@Composable
fun MetricTtsReadout(
    enabled: Boolean,
    snapshot: TelemetrySnapshot,
    intervalMs: Long = 5_000L,
) {
    if (!enabled) return
    val context = LocalContext.current
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.getDefault()
            }
        }
        engine
    }
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
    var lastSpokenMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(snapshot.timestampMs, enabled) {
        if (!enabled) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (now - lastSpokenMs < intervalMs) return@LaunchedEffect
        lastSpokenMs = now
        val beta = snapshot.driftAngleDeg?.let { "%.0f degrees beta".format(abs(it)) } ?: ""
        val speedKmh = snapshot.speedMps * 3.6f
        val message = buildString {
            append("Speed %.0f kilometers per hour. ".format(speedKmh))
            append("Lateral G %.2f. ".format(abs(snapshot.latG)))
            if (beta.isNotBlank()) append(beta)
        }
        tts.speak(message.trim(), TextToSpeech.QUEUE_FLUSH, null, "metric_readout")
    }
}
