package dev.foss.expeditiongauge.playback

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/** Longitudinal driving colors: green accel, yellow coast, red brake. */
object DrivingRouteStyling {
    const val BRAKE_THRESHOLD_G = -0.25f
    const val ACCEL_THRESHOLD_G = 0.25f

    val Coast = Color(0xFFFFCC00)
    val Brake = Color(0xFFFF3333)
    val Accel = Color(0xFF33CC33)
    val SlipHighlight = Color(0xFFFF6600)

    const val COAST_BUCKET = 0
    const val BRAKE_BUCKET = 1
    const val ACCEL_BUCKET = 2

    fun colorBucket(lonAccel: Float): Int = when {
        lonAccel < BRAKE_THRESHOLD_G -> BRAKE_BUCKET
        lonAccel > ACCEL_THRESHOLD_G -> ACCEL_BUCKET
        else -> COAST_BUCKET
    }

    fun colorForBucket(bucket: Int): Color = when (bucket) {
        BRAKE_BUCKET -> Brake
        ACCEL_BUCKET -> Accel
        else -> Coast
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

    fun bucketToArgb(bucket: Int): Long = colorForBucket(bucket).toArgb().toLong() and 0xFFFFFFFFL
}

/** @deprecated Use [DrivingRouteStyling]; kept for heatmap/slip helpers during migration. */
object DriftRouteStyling {
    val DriftNeutral = DrivingRouteStyling.Coast
    val LonAccelBrake = DrivingRouteStyling.Brake
    val LonAccelAccel = DrivingRouteStyling.Accel
    val SlipHighlight = DrivingRouteStyling.SlipHighlight

    const val NEUTRAL_BUCKET = DrivingRouteStyling.COAST_BUCKET
    const val BRAKE_BUCKET = DrivingRouteStyling.BRAKE_BUCKET
    const val ACCEL_BUCKET = DrivingRouteStyling.ACCEL_BUCKET

    @Deprecated("Drift left/right buckets removed", level = DeprecationLevel.HIDDEN)
    const val LEFT_BUCKET = DrivingRouteStyling.COAST_BUCKET

    @Deprecated("Drift left/right buckets removed", level = DeprecationLevel.HIDDEN)
    const val RIGHT_BUCKET = DrivingRouteStyling.COAST_BUCKET

    fun colorBucket(beta: Float?, lonAccel: Float): Int = DrivingRouteStyling.colorBucket(lonAccel)

    fun colorForBucket(bucket: Int): Color = DrivingRouteStyling.colorForBucket(bucket)

    fun betaColor(beta: Float?): Color = DrivingRouteStyling.Coast

    fun widthBucket(latG: Float): Int = DrivingRouteStyling.widthBucket(latG)

    fun widthForBucket(bucket: Int): Dp = DrivingRouteStyling.widthForBucket(bucket)

    fun slipOverlayAlpha(slipRatio: Float?): Float = DrivingRouteStyling.slipOverlayAlpha(slipRatio)

    fun slipWidthDp(slipRatio: Float?): Dp = DrivingRouteStyling.slipWidthDp(slipRatio)

    fun betaToArgb(beta: Float?): Long = DrivingRouteStyling.bucketToArgb(DrivingRouteStyling.COAST_BUCKET)
}
