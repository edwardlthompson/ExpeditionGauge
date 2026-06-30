package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun AlertSummaryPanel(
    alerts: List<AlertEventEntity>,
    modifier: Modifier = Modifier,
) {
    if (alerts.isEmpty()) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingMd)
            .testTag("playback_alert_summary"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.playback_alerts_title, alerts.size),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleSmall,
        )
        alerts.take(20).forEach { alert ->
            Text(
                text = stringResource(
                    R.string.playback_alert_row,
                    alert.alertType,
                    alert.value,
                    alert.threshold,
                ),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
