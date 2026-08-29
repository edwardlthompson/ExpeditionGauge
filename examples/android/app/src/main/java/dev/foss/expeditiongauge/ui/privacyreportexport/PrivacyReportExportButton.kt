package dev.foss.expeditiongauge.ui.privacyreportexport

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.privacyreportexport.PrivacyReportExport

@Composable
fun PrivacyReportExportButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    TextButton(
        onClick = {
            val body = PrivacyReportExport.markdown(
                kind = "bug",
                description = "Settings privacy-report export",
                appVersion = "2.19.0",
            )
            context.startActivity(PrivacyReportExport.shareIntent(body))
        },
        modifier = modifier.testTag("privacy_report_export"),
    ) {
        Text(stringResource(R.string.privacy_report_export))
    }
}
