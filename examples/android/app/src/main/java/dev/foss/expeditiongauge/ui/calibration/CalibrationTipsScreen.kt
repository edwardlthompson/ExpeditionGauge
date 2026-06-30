package dev.foss.expeditiongauge.ui.calibration

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun CalibrationTipsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.calibration_tips_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
            modifier = Modifier.testTag("calibration_tips_title"),
        )
        Text(text = stringResource(R.string.calibration_tips_intro), color = GaugeScaleWhite)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(2.dp, GaugeYellow)
                .padding(SpacingMd)
                .testTag("calibration_mount_diagram"),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.calibration_mount_diagram),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(text = stringResource(R.string.calibration_tips_level), color = GaugeScaleWhite)
        Text(text = stringResource(R.string.calibration_tips_figure8), color = GaugeScaleWhite)
        Text(text = stringResource(R.string.calibration_tips_full_wizard), color = GaugeScaleWhite)
        Button(onClick = onBack, modifier = Modifier.testTag("calibration_tips_back")) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
