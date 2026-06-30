package dev.foss.expeditiongauge.playback

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

object DriftRouteStyling {
    val DriftNeutral = Color(0xFFFFCC00)
    val DriftLeft = Color(0xFF00CCFF)
    val DriftRight = Color(0xFFFF00CC)
    val LonAccelBrake = Color(0xFFFF3333)
    val LonAccelAccel = Color(0xFF33CC33)
    val SlipHighlight = Color(0xFFFF6600)

    const val NEUTRAL_BUCKET = 0
    const val LEFT_BUCKET = 1
    const val RIGHT_BUCKET = 2
    const val BRAKE_BUCKET = 3
    const val ACCEL_BUCKET = 4

    fun colorBucket(beta: Float?, lonAccel: Float): Int {
        if (lonAccel < -0.4f) return BRAKE_BUCKET
        if (lonAccel > 0.35f) return ACCEL_BUCKET
        val drift = beta ?: return NEUTRAL_BUCKET
        return when {
            drift > 5f -> LEFT_BUCKET
            drift < -5f -> RIGHT_BUCKET
            else -> NEUTRAL_BUCKET
        }
    }

    fun colorForBucket(bucket: Int): Color = when (bucket) {
        LEFT_BUCKET -> DriftLeft
        RIGHT_BUCKET -> DriftRight
        BRAKE_BUCKET -> LonAccelBrake
        ACCEL_BUCKET -> LonAccelAccel
        else -> DriftNeutral
    }

    fun betaColor(beta: Float?): Color {
        val drift = beta ?: return DriftNeutral
        return when {
            drift > 5f -> DriftLeft
            drift < -5f -> DriftRight
            else -> DriftNeutral
        }
    }

    fun widthBucket(latG: Float): Int = when {
        abs(latG) >= 1.2f -> 3
        abs(latG) >= 0.7f -> 2
        abs(latG) >= 0.3f -> 1
        else -> 0
    }

    fun widthForBucket(bucket: Int): Dp = when (bucket) {
        3 -> 10.dp
        2 -> 7.dp
        1 -> 5.dp
        else -> 3.dp
    }

    fun slipOverlayAlpha(slipRatio: Float?): Float {
        val slip = slipRatio ?: return 0f
        return ((slip - 0.08f) / 0.25f).coerceIn(0f, 0.85f)
    }

    fun slipWidthDp(slipRatio: Float?): Dp {
        val slip = slipRatio ?: return 0.dp
        return (4.dp + (slip * 12f).dp).coerceAtMost(16.dp)
    }

    fun betaToArgb(beta: Float?): Long = betaColor(beta).toArgb().toLong() and 0xFFFFFFFFL
}
