package dev.foss.expeditiongauge.ui.crash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.foss.expeditiongauge.crash.PendingCrash
import dev.foss.expeditiongauge.crash.PendingCrashStore
import dev.foss.expeditiongauge.ui.feedback.FeedbackScreen

@Composable
fun PendingCrashReview(online: Boolean) {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<PendingCrash?>(null) }
    LaunchedEffect(Unit) {
        pending = PendingCrashStore(context).read()
    }
    val crash = pending ?: return
    FeedbackScreen(
        kind = "crash",
        initialDescription = crash.message,
        stack = crash.stack,
        online = online,
        onDismiss = {
            PendingCrashStore(context).clear()
            pending = null
        },
    )
}
