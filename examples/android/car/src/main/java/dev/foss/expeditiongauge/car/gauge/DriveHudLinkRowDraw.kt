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
    val alertColor = theme.alertText
    val primary = paint(
        if (speedAlert) alertColor else theme.primaryText,
        size * 0.13f,
        bold = true,
    )
    // HDG / elev / lat/lon share one size; bumped to fill space above the link row.
    val secondary = paint(theme.secondaryText, size * 0.11f, bold = true)
    fit(primary, speed, size * 0.88f)
    fit(secondary, heading, size * 0.96f)
    fit(secondary, alt, size * 0.96f)
    coordLines.forEach { fit(secondary, it, size * 0.96f) }

    val linkRowH = size * 0.14f
    val pedalH = size * 0.09f
    val inset = size * 0.045f
    val gap = size * 0.015f
    val linkTop = y + size - inset - linkRowH
    val pedalTop = linkTop - gap - pedalH
    val lines = buildList {
        add(primary to speed)
        add(secondary to heading)
        add(secondary to alt)
        coordLines.forEach { add(secondary to it) }
    }
    val cx = x + size / 2f
    var cursor = y + inset
    lines.forEach { (p, text) ->
        val lineH = p.descent() - p.ascent()
        if (cursor + lineH > pedalTop - gap) return@forEach
        canvas.drawText(text, cx, cursor - p.ascent(), p)
        cursor += lineH + gap
    }
    drawPedalBar(
        x = x + inset,
        y = pedalTop,
        width = size - inset * 2f,
        height = pedalH,
        state = PedalBarLogic.from(pedalThrottlePct, pedalLonG),
        flashOn = pedalFlashOn,
    )
    drawLinkRow(
        x = x,
        y = linkTop,
        width = size.toFloat(),
        height = linkRowH,
        theme = theme,
        gpsLinked = gpsLinked,
        obdLinked = obdLinked,
        tpmsLinked = tpmsLinked,
        imuLinked = imuLinked,
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
    val iconSize = height * 0.72f
    icons.forEachIndexed { index, (linked, drawer) ->
        iconPaint.color = if (linked) theme.primaryText else theme.dimText
        val cx = x + step * (index + 1)
        val cy = y + height * 0.5f
        drawer(canvas, cx, cy, iconSize, iconPaint)
    }
}
