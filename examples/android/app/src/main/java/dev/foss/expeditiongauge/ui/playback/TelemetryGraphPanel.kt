package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.GraphSeries
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import dev.foss.expeditiongauge.playback.PlaybackState
import dev.foss.expeditiongauge.playback.TelemetryGraphRenderer
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackAlertLine
import dev.foss.expeditiongauge.ui.theme.SpacingMd

enum class GraphTab { SPEED, ATTITUDE, TIRES }

@Composable
fun TelemetryGraphPanel(
    state: PlaybackState,
    modifier: Modifier = Modifier,
    onSeek: (Int) -> Unit = {},
) {
    var tab by remember { mutableIntStateOf(0) }
    val samples = state.samples
    val series = remember(samples, tab) {
        when (tab) {
            GraphTab.SPEED.ordinal -> TelemetryGraphRenderer.speedTabSeries(samples)
            GraphTab.ATTITUDE.ordinal -> TelemetryGraphRenderer.attitudeTabSeries(samples)
            else -> TelemetryGraphRenderer.tireTabSeries(samples)
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .testTag("telemetry_graph_panel"),
    ) {
        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == GraphTab.SPEED.ordinal,
                onClick = { tab = GraphTab.SPEED.ordinal },
                text = { Text(stringResource(R.string.graph_tab_speed)) },
            )
            Tab(
                selected = tab == GraphTab.ATTITUDE.ordinal,
                onClick = { tab = GraphTab.ATTITUDE.ordinal },
                text = { Text(stringResource(R.string.graph_tab_attitude)) },
            )
            Tab(
                selected = tab == GraphTab.TIRES.ordinal,
                onClick = { tab = GraphTab.TIRES.ordinal },
                text = { Text(stringResource(R.string.graph_tab_tires)) },
            )
        }
        GraphLegend(series = series)
        if (series.isEmpty()) {
            Text(
                text = stringResource(R.string.graph_no_data),
                modifier = Modifier.padding(SpacingMd),
            )
        } else {
            GraphCanvas(
                series = series,
                cursorIndex = state.currentIndex,
                totalPoints = samples.size,
                alertIndices = state.markers
                    .filter { it.type == ScrubberMarkerType.ALERT }
                    .map { it.sampleIndex },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(SpacingMd)
                    .testTag("telemetry_graph_canvas")
                    .pointerInput(samples.size) {
                        detectTapGestures { offset ->
                            if (samples.isEmpty()) return@detectTapGestures
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            onSeek((fraction * samples.lastIndex).toInt())
                        }
                    },
            )
        }
    }
}

@Composable
private fun GraphCanvas(
    series: List<GraphSeries>,
    cursorIndex: Int,
    totalPoints: Int,
    alertIndices: List<Int> = emptyList(),
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (series.isEmpty() || totalPoints == 0) return@Canvas
        val w = size.width
        val h = size.height
        series.forEach { s ->
            if (s.values.isEmpty()) return@forEach
            val min = s.values.min()
            val max = s.values.max()
            val range = (max - min).coerceAtLeast(0.01f)
            val path = Path()
            s.values.forEachIndexed { i, v ->
                val x = i.toFloat() / s.values.lastIndex.coerceAtLeast(1) * w
                val y = h - ((v - min) / range) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            val strokeColor = if (s.color != Color.Unspecified) s.color else GaugeYellow
            drawPath(path, strokeColor, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
        }
        alertIndices.forEach { index ->
            val alertX = index.toFloat() / totalPoints.coerceAtLeast(1) * w
            drawLine(PlaybackAlertLine, Offset(alertX, 0f), Offset(alertX, h), strokeWidth = 1.5f)
        }
        val cursorX = cursorIndex.toFloat() / totalPoints.coerceAtLeast(1) * w
        drawLine(Color.Red, Offset(cursorX, 0f), Offset(cursorX, h), strokeWidth = 2f)
    }
}
