package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp

/**
 * Text that shrinks to fit the available box. Never ellipsizes away content —
 * font size is reduced until the full string fits (within [minSp]).
 */
@Composable
fun HudAutoFitText(
    text: String,
    color: Color,
    style: TextStyle,
    minSp: Float,
    maxSp: Float,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
) {
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val maxHeightPx = constraints.maxHeight
        val fittedSp = remember(text, style, minSp, maxSp, maxWidthPx, maxHeightPx, maxLines) {
            fitHudTextSp(measurer, listOf(text), style, minSp, maxSp, maxWidthPx, maxHeightPx, maxLines)
        }
        Text(
            text = text,
            color = color,
            style = style.copy(fontSize = fittedSp.sp, lineHeight = (fittedSp * 1.2f).sp),
            maxLines = maxLines,
            softWrap = maxLines > 1,
            overflow = TextOverflow.Clip,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** One size that fits every line in [texts] — same rule as the AA telemetry cube. */
fun fitHudTextSp(
    measurer: TextMeasurer,
    texts: List<String>,
    style: TextStyle,
    minSp: Float,
    maxSp: Float,
    maxWidthPx: Int,
    maxHeightPx: Int,
    maxLines: Int = 1,
): Float {
    val lines = texts.filter { it.isNotBlank() }
    val floor = minSp.coerceAtLeast(8f)
    val ceiling = maxSp.coerceAtLeast(floor)
    if (lines.isEmpty() || maxWidthPx == Constraints.Infinity || maxWidthPx <= 0) return ceiling
    val heightCap = if (maxHeightPx == Constraints.Infinity || maxHeightPx <= 0) {
        Int.MAX_VALUE
    } else {
        maxHeightPx
    }
    var lo = floor
    var hi = ceiling
    var best = floor
    while (hi - lo > 0.35f) {
        val mid = (lo + hi) / 2f
        val midStyle = style.copy(fontSize = mid.sp, lineHeight = (mid * 1.2f).sp)
        val fits = lines.all { line ->
            val layout = measurer.measure(
                text = line,
                style = midStyle,
                maxLines = maxLines,
                softWrap = maxLines > 1,
                overflow = TextOverflow.Clip,
                constraints = Constraints(maxWidth = maxWidthPx),
            )
            !layout.hasVisualOverflow &&
                layout.size.width <= maxWidthPx &&
                layout.size.height <= heightCap
        }
        if (fits) {
            best = mid
            lo = mid
        } else {
            hi = mid
        }
    }
    return best
}
