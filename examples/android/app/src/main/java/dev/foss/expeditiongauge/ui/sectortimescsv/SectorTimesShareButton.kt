package dev.foss.expeditiongauge.ui.sectortimescsv

import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.sectortimescsv.SectorTimesCsv
import dev.foss.expeditiongauge.ui.navigation.shareExportFile
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SectorTimesShareButton(
    sessionId: Long,
    database: ExpeditionGaugeDatabase,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = {
            scope.launch {
                val laps = database.lapDao().getBySession(sessionId)
                val splits = laps.flatMap { database.sectorSplitDao().getByLap(it.id) }
                val file = File(context.cacheDir, "session_${sessionId}_sectors.csv")
                file.writeText(SectorTimesCsv.export(splits))
                shareExportFile(context, file, "text/csv")
            }
        },
        modifier = modifier.testTag("sector_times_share"),
    ) {
        Text(stringResource(R.string.sector_times_csv_share))
    }
}
