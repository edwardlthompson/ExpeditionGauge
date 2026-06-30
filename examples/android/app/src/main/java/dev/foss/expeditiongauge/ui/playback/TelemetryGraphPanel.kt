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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.playback.PlaybackState
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

enum class GraphTab { SPEED, ATTITUDE, TIRES }

data class GraphSeries(
    val label: String,
    val values: List<Float>,
    val color: Color,
)

object TelemetryGraphRenderer {
    private const val MAX_POINTS = 2000

    fun decimate(samples: List<SampleEntity>, selector: (SampleEntity) -> Float?): GraphSeries? {
        val points = samples.mapNotNull { s -> selector(s)?.let { s.timestampMs to it } }
        if (points.isEmpty()) return null
        val step = (points.size / MAX_POINTS).coerceAtLeast(1)
        val decimated = points.filterIndexed { index, _ -> index % step == 0 }
        return GraphSeries(
            label = "",
            values = decimated.map { it.second },
            color = Color.Unspecified,
        )
    }

    fun speedSeries(samples: List<SampleEntity>): GraphSeries? =
        decimate(samples) { it.speedMps * 3.6f }?.copy(label = "speed")

    fun latGSeries(samples: List<SampleEntity>): GraphSeries? =
        decimate(samples) { it.latG }?.copy(label = "latG")
}

@Composable
fun TelemetryGraphPanel(
    state: PlaybackState,
    modifier: Modifier = Modifier,
    onSeek: (Int) -> Unit = {},
) {
    var tab by remember { mutableIntStateOf(0) }
    val samples = state.samples
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(stringResource(R.string.graph_tab_speed)) })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(stringResource(R.string.graph_tab_attitude)) })
            Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text(stringResource(R.string.graph_tab_tires)) })
        }
        val series = when (tab) {
            0 -> listOfNotNull(TelemetryGraphRenderer.speedSeries(samples))
            1 -> listOfNotNull(
                TelemetryGraphRenderer.latGSeries(samples),
                TelemetryGraphRenderer.decimate(samples) { it.pitchDeg },
            )
            else -> emptyList()
        }
        GraphCanvas(
            series = series,
            cursorIndex = state.currentIndex,
            totalPoints = samples.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(SpacingMd)
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

@Composable
private fun GraphCanvas(
    series: List<GraphSeries>,
    cursorIndex: Int,
    totalPoints: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (series.isEmpty() || totalPoints == 0) return@Canvas
        val w = size.width
        val h = size.height
        series.forEachIndexed { idx, s ->
            if (s.values.isEmpty()) return@forEachIndexed
            val min = s.values.min()
            val max = s.values.max()
            val range = (max - min).coerceAtLeast(0.01f)
            val path = Path()
            s.values.forEachIndexed { i, v ->
                val x = i.toFloat() / s.values.lastIndex.coerceAtLeast(1) * w
                val y = h - ((v - min) / range) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, if (idx == 0) GaugeYellow else Color.Cyan, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
        }
        val cursorX = cursorIndex.toFloat() / totalPoints.coerceAtLeast(1) * w
        drawLine(Color.Red, Offset(cursorX, 0f), Offset(cursorX, h), strokeWidth = 2f)
    }
}
