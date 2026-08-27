package dev.foss.expeditiongauge.car.gauge

import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color

/** Horizontal brake←center→throttle fills; two needles (both pedals can be in). */
internal fun DriveHudCubeDraw.drawPedalBar(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    state: PedalBarState,
    flashOn: Boolean,
) {
    val track = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF3A424C.toInt() }
    val rect = RectF(x, y, x + width, y + height)
    val r = height * 0.45f
    canvas.drawRoundRect(rect, r, r, track)
    val mid = x + width / 2f
    val half = width / 2f
    val hideTh = state.flashThrottle && !flashOn
    val hideBr = state.flashBrake && !flashOn
    val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    if (!hideTh && state.throttle01 > 0f) {
        fill.color = lerpColor(0xFF8A8F96.toInt(), 0xFF14E06A.toInt(), state.throttle01)
        canvas.drawRoundRect(RectF(mid, y, mid + half * state.throttle01, y + height), r, r, fill)
    }
    if (!hideBr && state.brake01 > 0f) {
        fill.color = lerpColor(0xFF8A8F96.toInt(), 0xFFFF2A2A.toInt(), state.brake01)
        canvas.drawRoundRect(RectF(mid - half * state.brake01, y, mid, y + height), r, r, fill)
    }
    val nest = height * 0.22f
    val needle = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    if (!hideTh) {
        drawPedalNeedle(mid + half * state.throttle01 + nest, y, height, needle)
    }
    if (!hideBr) {
        drawPedalNeedle(mid - half * state.brake01 - nest, y, height, needle)
    }
}

private fun DriveHudCubeDraw.drawPedalNeedle(cx: Float, y: Float, height: Float, paint: Paint) {
    val half = (height * 0.14f).coerceAtLeast(1f)
    canvas.drawRoundRect(
        RectF(cx - half, y, cx + half, y + height),
        height * 0.12f,
        height * 0.12f,
        paint,
    )
}

private fun lerpColor(from: Int, to: Int, t: Float): Int {
    val u = t.coerceIn(0f, 1f)
    val a = Color.alpha(from) + ((Color.alpha(to) - Color.alpha(from)) * u).toInt()
    val r = Color.red(from) + ((Color.red(to) - Color.red(from)) * u).toInt()
    val g = Color.green(from) + ((Color.green(to) - Color.green(from)) * u).toInt()
    val b = Color.blue(from) + ((Color.blue(to) - Color.blue(from)) * u).toInt()
    return Color.argb(a, r, g, b)
}
