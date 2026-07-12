package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HudClockLabels(val time: String, val date: String)

/** Wall-clock time + date that ticks on each second boundary. */
@Composable
fun rememberHudClockLabels(): HudClockLabels {
    val timeFmt = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    var labels by remember {
        val now = Date()
        mutableStateOf(HudClockLabels(timeFmt.format(now), dateFmt.format(now)))
    }
    LaunchedEffect(Unit) {
        while (true) {
            val nowMs = System.currentTimeMillis()
            val now = Date(nowMs)
            labels = HudClockLabels(timeFmt.format(now), dateFmt.format(now))
            delay((1000L - (nowMs % 1000L)).coerceIn(1L, 1000L))
        }
    }
    return labels
}

/** @deprecated Prefer [rememberHudClockLabels]. */
@Composable
fun rememberHudClockLabel(): String = rememberHudClockLabels().time
