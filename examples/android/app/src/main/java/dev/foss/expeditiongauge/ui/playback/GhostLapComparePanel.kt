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
import dev.foss.expeditiongauge.ghost.GhostLapComparer
import dev.foss.expeditiongauge.ghost.GhostLapOverlay
import dev.foss.expeditiongauge.ghost.SectorDeltaRow
import dev.foss.expeditiongauge.ghostsectorcompare.GhostSectorCompare
import dev.foss.expeditiongauge.timing.LapTimingSummary
import dev.foss.expeditiongauge.timing.PredictiveTimingEngine
import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun GhostLapComparePanel(
    summary: LapTimingSummary?,
    primaryLap: LapEntity?,
    ghostLap: LapEntity?,
    ghostDeltaMs: Long?,
    trackMismatch: Boolean,
    ghostLapNumber: Int?,
    modifier: Modifier = Modifier,
) {
    if (trackMismatch) {
        Text(
            text = stringResource(R.string.ghost_lap_mismatch),
            color = GaugeYellow,
            modifier = modifier
                .fillMaxWidth()
                .testTag("ghost_lap_mismatch"),
        )
        return
    }
    if (ghostLap == null || summary == null) return
    val formatter = PredictiveTimingEngine()
    val overlay = GhostLapOverlay()
    val sectorRows = primaryLap?.let { primary ->
        val primarySplits = summary.splitsByLap[primary.id].orEmpty()
        val ghostSplits = summary.splitsByLap[ghostLap.id].orEmpty()
        GhostSectorCompare.rows(primarySplits, ghostSplits)
    }.orEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingMd)
            .testTag("ghost_lap_compare_panel"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(
                R.string.ghost_lap_title,
                ghostLapNumber ?: ghostLap.lapNumber,
            ),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(
                R.string.ghost_lap_delta,
                overlay.formatDelta(ghostDeltaMs),
            ),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("ghost_lap_delta"),
        )
        if (sectorRows.isNotEmpty()) {
            Text(
                text = stringResource(R.string.ghost_lap_sector_compare),
                color = GaugeYellow,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = stringResource(
                    R.string.ghost_sector_net,
                    overlay.formatDelta(GhostSectorCompare.netDeltaMs(sectorRows)),
                    GhostSectorCompare.fastestSectorCount(sectorRows),
                ),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("ghost_sector_net"),
            )
            sectorRows.forEach { row ->
                SectorDeltaRowText(row = row, formatter = formatter)
            }
        }
    }
}

@Composable
private fun SectorDeltaRowText(
    row: SectorDeltaRow,
    formatter: PredictiveTimingEngine,
) {
    Text(
        text = stringResource(
            R.string.ghost_lap_sector_row,
            row.sectorIndex + 1,
            formatter.formatLapTime(row.primaryMs),
            formatter.formatLapTime(row.ghostMs),
            formatter.formatLapTime(kotlin.math.abs(row.deltaMs)),
        ),
        color = GaugeScaleWhite.copy(alpha = 0.85f),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(start = SpacingMd),
    )
}

fun resolveGhostCompareLaps(
    summary: LapTimingSummary?,
    samples: List<SampleEntity>,
    currentIndex: Int,
    ghostLapNumber: Int?,
): Pair<LapEntity?, LapEntity?> {
    if (summary == null) return null to null
    val ghost = summary.laps.firstOrNull { it.lapNumber == ghostLapNumber }
        ?: lapTimingServiceBestLap(summary)
    val primary = GhostLapComparer.lapForSample(summary.laps, samples.getOrNull(currentIndex))
    return primary to ghost
}

private fun lapTimingServiceBestLap(summary: LapTimingSummary): LapEntity? =
    summary.laps.filter { it.isValid && !it.isOutLap }.minByOrNull { it.durationMs }
