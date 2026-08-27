package dev.foss.expeditiongauge.car.gauge

import android.graphics.Paint

/** Link-row + telemetry-cube extras for [DriveHudCubeDraw]. */
internal fun DriveHudCubeDraw.drawTelemetryCube(
    x: Int, y: Int, size: Int, theme: DriveHudTheme,
    speed: String, heading: String, alt: String, coords: String,
    gpsLinked: Boolean = false,
    obdLinked: Boolean = false,
    tpmsLinked: Boolean = false,
    imuLinked: Boolean = false,
    speedAlert: Boolean = false,
) {
    drawCubeChrome(x, y, size, theme)
    val coordLines = coords.lines().filter { it.isNotBlank() }.take(2)
    val body = listOf(heading, alt) + coordLines
    val slots = TelemetryCubeLayout.compute(size, 1 + body.size)
    val primary = paint(
        if (speedAlert) theme.alertText else theme.primaryText,
        slots.primarySize,
        bold = true,
    )
    val secondary = paint(theme.secondaryText, slots.secondarySize, bold = true)
    fit(primary, speed, size * 0.92f)
    body.forEach { fit(secondary, it, size * 0.96f) }
    val cx = x + size / 2f
    var cursor = y + slots.inset
    val stack = listOf(primary to speed) + body.map { secondary to it }
    stack.forEach { (p, text) ->
        val lineH = p.descent() - p.ascent()
        canvas.drawText(text, cx, cursor - p.ascent(), p)
        cursor += lineH + slots.gap
    }
    drawLinkRow(
        x = x,
        y = y + slots.linkY,
        width = size.toFloat(),
        height = slots.linkH,
        theme = theme,
        gpsLinked = gpsLinked,
        obdLinked = obdLinked,
        tpmsLinked = tpmsLinked,
        imuLinked = imuLinked,
    )
    drawPedalBar(
        x = x + slots.inset,
        y = y + slots.pedalY,
        width = size - slots.inset * 2f,
        height = slots.pedalH,
        state = PedalBarLogic.from(pedalThrottlePct, pedalLonG),
        flashOn = pedalFlashOn,
    )
}

internal fun DriveHudCubeDraw.drawLinkRow(
    x: Int,
    y: Float,
    width: Float,
    height: Float,
    theme: DriveHudTheme,
    gpsLinked: Boolean,
    obdLinked: Boolean,
    tpmsLinked: Boolean,
    imuLinked: Boolean,
) {
    val icons = listOf(
        gpsLinked to DriveHudLinkIcons::drawGps,
        obdLinked to DriveHudLinkIcons::drawObd,
        tpmsLinked to DriveHudLinkIcons::drawTpms,
        imuLinked to DriveHudLinkIcons::drawImu,
    )
    val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    val step = width / (icons.size + 1)
    val iconSize = height
    icons.forEachIndexed { index, (linked, drawer) ->
        iconPaint.color = if (linked) theme.primaryText else theme.dimText
        val cx = x + step * (index + 1)
        val cy = y + height * 0.5f
        drawer(canvas, cx, cy, iconSize, iconPaint)
    }
}
