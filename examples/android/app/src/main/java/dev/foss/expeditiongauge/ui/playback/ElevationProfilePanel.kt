package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.playback.ElevationProfileBuilder
import dev.foss.expeditiongauge.playback.PlaybackState
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackElevationFill
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun ElevationProfilePanel(
    state: PlaybackState,
    modifier: Modifier = Modifier,
    onSeek: (Int) -> Unit = {},
) {
    ElevationProfilePanelContent(
        samples = state.samples,
        currentIndex = state.currentIndex,
        modifier = modifier,
        onSeek = onSeek,
    )
}

@Composable
fun ElevationProfilePanelContent(
    samples: List<SampleEntity>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    onSeek: (Int) -> Unit = {},
) {
    val profile = remember(samples) { ElevationProfileBuilder.build(samples) }
    if (profile == null || !profile.hasProfile) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMd)
            .testTag("elevation_profile_panel"),
    ) {
        Text(
            text = stringResource(R.string.playback_elevation),
            color = GaugeYellow,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = stringResource(
                R.string.playback_elevation_stats,
                profile.minM.toInt(),
                profile.maxM.toInt(),
                profile.totalAscentM.toInt(),
                profile.totalDescentM.toInt(),
            ),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .testTag("elevation_profile_stats")
                .semantics {
                    contentDescription =
                        "elevation min ${profile.minM.toInt()} max ${profile.maxM.toInt()}"
                },
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(top = SpacingMd)
                .testTag("elevation_profile_canvas")
                .pointerInput(samples.size) {
                    detectTapGestures { offset ->
                        if (samples.isEmpty()) return@detectTapGestures
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeek((fraction * samples.lastIndex).toInt())
                    }
                },
        ) {
            val altitudes = profile.smoothedAltitudesM
            val minAlt = profile.minM
            val maxAlt = profile.maxM
            val range = (maxAlt - minAlt).coerceAtLeast(1.0)
            val w = size.width
            val h = size.height
            val path = Path()
            altitudes.forEachIndexed { index, alt ->
                val x = index.toFloat() / altitudes.lastIndex.coerceAtLeast(1) * w
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
