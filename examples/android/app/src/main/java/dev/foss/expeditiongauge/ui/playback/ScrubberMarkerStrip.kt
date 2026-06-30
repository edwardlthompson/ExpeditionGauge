package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import dev.foss.expeditiongauge.ui.theme.PlaybackScrubberAlert
import dev.foss.expeditiongauge.ui.theme.PlaybackScrubberHighBeta
import dev.foss.expeditiongauge.ui.theme.PlaybackScrubberHighSlip
import dev.foss.expeditiongauge.ui.theme.PlaybackScrubberLapCrossing
import dev.foss.expeditiongauge.ui.theme.PlaybackScrubberMarkEvent

@Composable
fun ScrubberMarkerStrip(
    markers: List<ScrubberMarker>,
    totalSamples: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (markers.isEmpty() || totalSamples <= 1) return
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .testTag("playback_scrubber_markers")
            .pointerInput(markers, totalSamples) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek((fraction * (totalSamples - 1)).toInt())
                }
            },
    ) {
        val h = size.height
        markers.forEach { marker ->
            val x = marker.sampleIndex.toFloat() / (totalSamples - 1).coerceAtLeast(1) * size.width
            drawCircle(
                color = marker.type.color(),
                radius = 4f,
                center = Offset(x, h / 2f),
            )
        }
    }
}

private fun ScrubberMarkerType.color(): Color = when (this) {
    ScrubberMarkerType.HIGH_BETA -> PlaybackScrubberHighBeta
    ScrubberMarkerType.HIGH_SLIP -> PlaybackScrubberHighSlip
    ScrubberMarkerType.ALERT -> PlaybackScrubberAlert
    ScrubberMarkerType.LAP_CROSSING -> PlaybackScrubberLapCrossing
    ScrubberMarkerType.MARK_EVENT -> PlaybackScrubberMarkEvent
}
