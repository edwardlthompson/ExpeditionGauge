package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.hypot

private const val SwipeToggleThresholdPx = 72f

/**
 * Tap or swipe advances the attitude display cycle; long-press opens calibrate.
 */
fun Modifier.attitudeGaugeInteraction(
    onToggleDisplay: () -> Unit,
    onLongPressCalibrate: () -> Unit,
): Modifier = this
    .pointerInput(onToggleDisplay, onLongPressCalibrate) {
        detectTapGestures(
            onTap = { onToggleDisplay() },
            onLongPress = { onLongPressCalibrate() },
        )
    }
    .pointerInput(onToggleDisplay) {
        var totalX = 0f
        var totalY = 0f
        detectDragGestures(
            onDragStart = {
                totalX = 0f
                totalY = 0f
            },
            onDragEnd = {
                if (hypot(totalX.toDouble(), totalY.toDouble()) >= SwipeToggleThresholdPx) {
                    onToggleDisplay()
                }
            },
            onDragCancel = {
                totalX = 0f
                totalY = 0f
            },
            onDrag = { change, dragAmount ->
                change.consume()
                totalX += dragAmount.x
                totalY += dragAmount.y
            },
        )
    }
