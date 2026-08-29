package dev.foss.expeditiongauge.display

import android.os.Build
import android.view.View
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import dev.foss.expeditiongauge.composepreferredframerate.PreferredFrameRate

/** Vote HIGH refresh when the platform exposes requestedFrameRate (API 35+). */
fun Modifier.highRefreshScroll(): Modifier = composed {
    val view = LocalView.current
    SideEffect {
        if (PreferredFrameRate.isHighVote(Build.VERSION.SDK_INT)) {
            view.requestedFrameRate = View.REQUESTED_FRAME_RATE_CATEGORY_HIGH
        }
    }
    this
}
