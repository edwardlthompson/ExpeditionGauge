package dev.foss.expeditiongauge.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.DrivingRouteStyling
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.stats.SessionThumbnailGenerator
import dev.foss.expeditiongauge.ui.playback.activityTypeLabel
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackMapRouteCasing
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RichSessionCard(
    summary: SessionStatsSummary,
    onPlay: () -> Unit,
    onCompare: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("session_card_${summary.sessionId}")
            .semantics { contentDescription = "session ${summary.name}" },
    ) {
        Row(
            modifier = Modifier.padding(SpacingMd),
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            SessionRouteThumbnail(
                routePoints = summary.routeThumb,
                routeSegments = summary.routeThumbSegments,
                sparkline = summary.sparklineLatG,
                modifier = Modifier
                    .width(72.dp)
                    .height(56.dp)
                    .testTag("session_map_thumb"),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = summary.name, color = GaugeYellow, style = MaterialTheme.typography.titleMedium)
                if (FeatureFlags.activityLibraryEnabled) {
                    Text(
                        text = stringResource(
                            R.string.activity_type_label,
                            activityTypeLabel(summary.activityType),
                        ),
                        color = GaugeScaleWhite,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.testTag("session_activity_type"),
                    )
                }
                Text(
                    text = stringResource(R.string.stats_duration, summary.durationMs / 1000),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(R.string.stats_peak_g, summary.peakLatG ?: 0f),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(R.string.stats_max_beta, summary.maxBetaDeg ?: 0f),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(R.string.stats_slip_events, summary.slipEventCount),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (summary.eventCount > 0) {
                    Text(
                        text = stringResource(R.string.stats_marked_events, summary.eventCount),
                        color = GaugeYellow,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("session_marked_events"),
                    )
                }
                summary.bestLapMs?.let { lapMs ->
                    Text(
                        text = stringResource(R.string.stats_best_lap, formatLap(lapMs)),
                        color = GaugeGreen,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
                        verticalArrangement = Arrangement.spacedBy(SpacingMd),
                    ) {
                        Button(onClick = onPlay, modifier = Modifier.testTag("session_play")) {
                            Text(stringResource(R.string.stats_play))
                        }
                        onCompare?.let { compare ->
                            Button(onClick = compare, modifier = Modifier.testTag("session_compare")) {
                                Text(stringResource(R.string.stats_compare_session))
                            }
                        }
                        onExport?.let { export ->
                            Button(onClick = export, modifier = Modifier.testTag("session_export_zip")) {
                                Text(stringResource(R.string.export_zip))
                            }
                        }
                        onEdit?.let { edit ->
                            Button(
                                onClick = edit,
                                modifier = Modifier
                                    .testTag("session_edit")
                                    .semantics { contentDescription = "Edit metadata" },
                            ) {
                                Text(stringResource(R.string.session_metadata_edit))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRouteThumbnail(
    routePoints: List<Pair<Float, Float>>,
    routeSegments: List<SessionThumbnailGenerator.ColoredSegment>,
    sparkline: List<Float>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.semantics { contentDescription = "route preview" }) {
        if (routeSegments.size >= 1) {
            drawRect(color = PlaybackMapRouteCasing)
            routeSegments.forEach { segment ->
                val color = DrivingRouteStyling.colorForBucket(segment.bucket)
                drawLine(
                    color = color,
                    start = Offset(segment.from.first * size.width, segment.from.second * size.height),
                    end = Offset(segment.to.first * size.width, segment.to.second * size.height),
                    strokeWidth = 2f,
                )
            }
            return@Canvas
        }
        if (routePoints.size >= 2) {
            drawRect(color = PlaybackMapRouteCasing)
            var previous: Offset? = null
            routePoints.forEach { (nx, ny) ->
                val point = Offset(nx * size.width, ny * size.height)
                previous?.let { drawLine(GaugeGreen, it, point, strokeWidth = 2f) }
                previous = point
            }
            return@Canvas
        }
        if (sparkline.size < 2) {
            drawRect(color = Color.DarkGray)
            return@Canvas
        }
        drawRect(color = PlaybackMapRouteCasing)
        val stepX = size.width / (sparkline.size - 1).coerceAtLeast(1)
        var previous: Offset? = null
        sparkline.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value.coerceIn(0f, 2f) / 2f * size.height)
            val point = Offset(x, y)
            previous?.let { drawLine(GaugeGreen, it, point, strokeWidth = 2f) }
            previous = point
        }
    }
}

private fun formatLap(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    val frac = (ms % 1000) / 10
    return if (min > 0) "%d:%02d.%02d".format(min, sec, frac) else "%d.%02d".format(sec, frac)
}
