package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.car.gauge.PedalBarLogic
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.PlaybackHeatmapNeutral
import dev.foss.expeditiongauge.ui.theme.PlaybackOffsetNeutral

@Composable
fun TelemetryHudPedalBar(
    throttlePct: Float?,
    lonG: Float,
    modifier: Modifier = Modifier,
) {
    val target = PedalBarLogic.from(throttlePct, lonG)
    val th by animateFloatAsState(target.throttle01, label = "throttle", animationSpec = tween(90))
    val br by animateFloatAsState(target.brake01, label = "brake", animationSpec = tween(90))
    val flash by rememberInfiniteTransition(label = "pedalFlash").animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "pedalBlink",
    )
    val desc = stringResource(R.string.gauge_pedal_bar)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .semantics { contentDescription = desc },
    ) {
        val r = size.height * 0.45f
        drawRoundRect(PlaybackHeatmapNeutral, cornerRadius = CornerRadius(r))
        val mid = size.width / 2f
        val hideTh = target.flashThrottle && flash < 0.45f
        val hideBr = target.flashBrake && flash < 0.45f
        if (!hideTh && th > 0f) {
            drawRoundRect(
                color = lerp(PlaybackOffsetNeutral, GaugeGreen, th),
                topLeft = Offset(mid, 0f),
                size = Size(mid * th, size.height),
                cornerRadius = CornerRadius(r),
            )
        }
        if (!hideBr && br > 0f) {
            val w = mid * br
            drawRoundRect(
                color = lerp(PlaybackOffsetNeutral, GaugeRed, br),
                topLeft = Offset(mid - w, 0f),
                size = Size(w, size.height),
                cornerRadius = CornerRadius(r),
            )
        }
        val nw = size.height * 0.42f
        val nest = size.height * 0.18f
        val nr = CornerRadius(size.height * 0.12f)
        if (!hideTh) {
            val nx = mid + mid * th + nest
            drawRoundRect(GaugeScaleWhite, Offset(nx - nw / 2f, 0f), Size(nw, size.height), nr)
        }
        if (!hideBr) {
            val nx = mid - mid * br - nest
            drawRoundRect(GaugeScaleWhite, Offset(nx - nw / 2f, 0f), Size(nw, size.height), nr)
        }
    }
}
