package dev.foss.expeditiongauge.ui.recording

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.liverecordgraphs.LiveGraphPoint
import dev.foss.expeditiongauge.liverecordgraphs.RecordingLiveGraph
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun RecordingLiveGraphStrip(
    snapshot: TelemetrySnapshot,
    modifier: Modifier = Modifier,
) {
    val buffer = remember { ArrayDeque<LiveGraphPoint>() }
    val points = remember(snapshot.timestampMs, snapshot.speedMps, snapshot.latG) {
        RecordingLiveGraph.retain(buffer, RecordingLiveGraph.pointFrom(snapshot))
        RecordingLiveGraph.decimate(buffer)
    }
    val label = stringResource(R.string.live_record_graph_cd)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = SpacingSm)
            .testTag("recording_live_graph")
            .semantics { contentDescription = label },
    ) {
        drawSeries(RecordingLiveGraph.speedSeries(points), Color(0xFFFFD700))
        drawSeries(RecordingLiveGraph.latGSeries(points), Color(0xFF88FF88))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSeries(
    values: List<Float>,
    color: Color,
) {
    if (values.size < 2) return
    val min = values.min()
    val max = values.max()
    val range = (max - min).coerceAtLeast(0.01f)
    val path = Path()
    values.forEachIndexed { i, v ->
        val x = i.toFloat() / values.lastIndex * size.width
        val y = size.height - ((v - min) / range) * size.height
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(path, color, style = Stroke(2f))
    val last = values.last()
    val lastX = size.width
    val lastY = size.height - ((last - min) / range) * size.height
    drawCircle(color, radius = 3f, center = Offset(lastX, lastY))
}
