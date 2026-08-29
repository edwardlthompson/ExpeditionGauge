package dev.foss.expeditiongauge.ui.nmealogexport

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.nmealogexport.NmeaLogExport
import dev.foss.expeditiongauge.ui.navigation.shareExportFile
import java.io.File

@Composable
fun NmeaLogShareButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        onClick = {
            val file = File(context.cacheDir, "nmea-log.nmea")
            file.writeText(NmeaLogExport.toFileText())
            shareExportFile(context, file, "text/plain")
        },
        modifier = modifier.testTag("nmea_log_share"),
    ) {
        Text(stringResource(R.string.nmea_log_share))
    }
}
