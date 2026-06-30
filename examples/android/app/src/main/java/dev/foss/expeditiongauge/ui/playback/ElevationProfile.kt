package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackElevationFill
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun ElevationProfile(
    samples: List<SampleEntity>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    val altitudes = samples.mapNotNull { it.altitudeM }
    if (altitudes.size < 2) return

    Column(modifier = modifier.padding(horizontal = SpacingMd)) {
        Text(
            text = stringResource(R.string.playback_elevation),
            color = GaugeYellow,
            style = MaterialTheme.typography.labelMedium,
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(top = SpacingMd),
        ) {
            val minAlt = altitudes.min()
            val maxAlt = altitudes.max()
            val range = (maxAlt - minAlt).coerceAtLeast(1.0)
            val w = size.width
            val h = size.height
            val path = Path()
            samples.forEachIndexed { index, sample ->
                val alt = sample.altitudeM ?: return@forEachIndexed
                val x = index.toFloat() / samples.lastIndex.coerceAtLeast(1) * w
                val y = h - ((alt - minAlt) / range).toFloat() * h
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color = GaugeYellow, style = Stroke(width = 2f))
            val fillPath = Path().apply {
                addPath(path)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(fillPath, color = PlaybackElevationFill)
            val cursorX = currentIndex.toFloat() / samples.lastIndex.coerceAtLeast(1) * w
            drawLine(Color.Red, Offset(cursorX, 0f), Offset(cursorX, h), strokeWidth = 2f)
        }
    }
}
