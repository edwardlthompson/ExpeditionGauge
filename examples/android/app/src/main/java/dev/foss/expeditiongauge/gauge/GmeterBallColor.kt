package dev.foss.expeditiongauge.gauge

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.hypot

private val BallGreen = Color(0xFF33FF33)
private val BallYellow = Color(0xFFFFDD00)
private val BallRed = Color(0xFFFF3333)

/**
 * Maps ball distance from center to a high-contrast green → yellow → red fill.
 */
object GmeterBallColor {
    fun normalizedDistance(ball: BallPosition): Float =
        hypot(ball.normalizedX.toDouble(), ball.normalizedY.toDouble()).toFloat().coerceIn(0f, 1f)

    fun fillColor(ball: BallPosition, alertActive: Boolean): Color {
        if (alertActive) return BallRed
        return colorForDistance(normalizedDistance(ball))
    }

    fun colorForDistance(distance: Float): Color {
        val d = distance.coerceIn(0f, 1f)
        return when {
            d <= 0.5f -> lerp(BallGreen, BallYellow, d / 0.5f)
            else -> lerp(BallYellow, BallRed, (d - 0.5f) / 0.5f)
        }
    }
}
