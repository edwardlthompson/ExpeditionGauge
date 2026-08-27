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
    val slots = TelemetryCubeLayout.compute(size)
    val coordLines = coords.lines().filter { it.isNotBlank() }
    val lat = coordLines.getOrElse(0) { "" }
    val lon = coordLines.getOrElse(1) { "" }
    val primary = paint(
        if (speedAlert) theme.alertText else theme.primaryText,
        slots.textSize,
        bold = true,
    )
    val secondary = paint(theme.secondaryText, slots.textSize, bold = true)
    val maxW = size * 0.92f
    listOf(speed, heading, alt, lat, lon).forEach { line ->
        if (line.isNotBlank()) fit(secondary, line, maxW)
    }
    primary.textSize = secondary.textSize
    val cx = x + size / 2f
    drawRowText(cx, y, slots, TelemetryCubeLayout.SPEED_ROW, speed, primary)
    drawRowText(cx, y, slots, TelemetryCubeLayout.HEADING_ROW, heading, secondary)
    drawRowText(cx, y, slots, TelemetryCubeLayout.ELEV_ROW, alt, secondary)
    drawRowText(cx, y, slots, TelemetryCubeLayout.LAT_ROW, lat, secondary)
    drawRowText(cx, y, slots, TelemetryCubeLayout.LON_ROW, lon, secondary)
    drawLinkRow(
        x = x,
        y = y + slots.rowTop(TelemetryCubeLayout.LINK_ROW),
        width = size.toFloat(),
        height = slots.rowH,
        iconSize = slots.iconSize,
        theme = theme,
        gpsLinked = gpsLinked,
        obdLinked = obdLinked,
        tpmsLinked = tpmsLinked,
        imuLinked = imuLinked,
    )
    val pedalTop = slots.rowTop(TelemetryCubeLayout.PEDAL_ROW) +
        (slots.rowH - slots.pedalH) / 2f
    drawPedalBar(
        x = x + slots.inset,
        y = y + pedalTop,
        width = size - slots.inset * 2f,
        height = slots.pedalH,
        state = PedalBarLogic.from(pedalThrottlePct, pedalLonG),
        flashOn = pedalFlashOn,
    )
}

private fun DriveHudCubeDraw.drawRowText(
    cx: Float,
    y: Int,
    slots: TelemetryCubeSlots,
    row: Int,
    text: String,
    paint: Paint,
) {
    if (text.isBlank()) return
    val top = y + slots.rowTop(row)
    val fm = paint.fontMetrics
    val textH = fm.descent - fm.ascent
    val baseline = top + (slots.rowH - textH) / 2f - fm.ascent
    canvas.drawText(text, cx, baseline, paint)
}

internal fun DriveHudCubeDraw.drawLinkRow(
    x: Int,
    y: Float,
    width: Float,
    height: Float,
    iconSize: Float,
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
    val glyph = iconSize.coerceAtMost(height)
    icons.forEachIndexed { index, (linked, drawer) ->
        iconPaint.color = if (linked) theme.primaryText else theme.dimText
        val cx = x + step * (index + 1)
        val cy = y + height * 0.5f
        drawer(canvas, cx, cy, glyph, iconPaint)
    }
}
