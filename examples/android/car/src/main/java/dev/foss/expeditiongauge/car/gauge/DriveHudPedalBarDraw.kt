package dev.foss.expeditiongauge.car.gauge

import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color

/** Horizontal brake←center→throttle fill for the AA telemetry cube. */
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
    val hide = (state.flashThrottle || state.flashBrake) && !flashOn
    if (!hide && state.position != 0f) {
        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
        if (state.position > 0f) {
            fill.color = lerpColor(0xFF8A8F96.toInt(), 0xFF14E06A.toInt(), state.throttle01)
            canvas.drawRoundRect(RectF(mid, y, mid + (width / 2f) * state.throttle01, y + height), r, r, fill)
        } else {
            fill.color = lerpColor(0xFF8A8F96.toInt(), 0xFFFF2A2A.toInt(), state.brake01)
            val left = mid - (width / 2f) * state.brake01
            canvas.drawRoundRect(RectF(left, y, mid, y + height), r, r, fill)
        }
    }
    val needle = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    val nx = mid + (width / 2f) * state.position
    canvas.drawRoundRect(
        RectF(nx - height * 0.18f, y - height * 0.08f, nx + height * 0.18f, y + height * 1.08f),
        height * 0.12f,
        height * 0.12f,
        needle,
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
