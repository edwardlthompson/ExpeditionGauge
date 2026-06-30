package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.playback.GraphSeries
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun GraphLegend(
    series: List<GraphSeries>,
    modifier: Modifier = Modifier,
) {
    if (series.isEmpty()) return
    Row(
        modifier = modifier.padding(horizontal = SpacingMd),
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        series.forEach { s ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                androidx.compose.foundation.Canvas(Modifier.size(10.dp)) {
                    drawCircle(s.color.takeIf { it != Color.Unspecified } ?: Color.White)
                }
                Text(text = s.label, color = GaugeScaleWhite, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}
