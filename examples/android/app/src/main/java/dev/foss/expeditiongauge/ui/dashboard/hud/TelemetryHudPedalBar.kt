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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.car.gauge.PedalBarLogic

@Composable
fun TelemetryHudPedalBar(
    throttlePct: Float?,
    lonG: Float,
    modifier: Modifier = Modifier,
) {
    val target = PedalBarLogic.from(throttlePct, lonG)
    val pos by animateFloatAsState(target.position, label = "pedal", animationSpec = tween(90))
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
            .height(14.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .semantics { contentDescription = desc },
    ) {
        val r = size.height * 0.45f
        drawRoundRect(Color(0xFF3A424C), cornerRadius = CornerRadius(r))
        val mid = size.width / 2f
        val hide = (target.flashThrottle || target.flashBrake) && flash < 0.45f
        if (!hide && pos != 0f) {
            if (pos > 0f) {
                val w = mid * target.throttle01
                drawRoundRect(
                    color = lerp(Color(0xFF8A8F96), Color(0xFF14E06A), target.throttle01),
                    topLeft = Offset(mid, 0f),
                    size = Size(w, size.height),
                    cornerRadius = CornerRadius(r),
                )
            } else {
                val w = mid * target.brake01
                drawRoundRect(
                    color = lerp(Color(0xFF8A8F96), Color(0xFFFF2A2A), target.brake01),
                    topLeft = Offset(mid - w, 0f),
                    size = Size(w, size.height),
                    cornerRadius = CornerRadius(r),
                )
            }
        }
        val nx = mid + mid * pos
        val nw = size.height * 0.36f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(nx - nw / 2f, -size.height * 0.08f),
            size = Size(nw, size.height * 1.16f),
            cornerRadius = CornerRadius(size.height * 0.12f),
        )
    }
}
