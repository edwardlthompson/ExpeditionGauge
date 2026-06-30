package dev.foss.expeditiongauge.ui.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun RecordingLiveStrip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GaugeRed)
            .testTag("recording_live_strip")
            .padding(vertical = SpacingSm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.recording_live_ready),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingAdvancedSheet(
    visible: Boolean,
    logIntervalHz: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.testTag("recording_advanced_sheet"),
    ) {
        Text(
            text = stringResource(R.string.recording_advanced_title),
            style = MaterialTheme.typography.titleMedium,
            color = GaugeScaleWhite,
            modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingSm),
        )
        Text(
            text = stringResource(R.string.recording_advanced_log_rate, logIntervalHz),
            color = GaugeScaleWhite,
            modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingSm),
        )
        Text(
            text = stringResource(R.string.recording_advanced_mark_hint),
            color = GaugeScaleWhite,
            modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingMd),
        )
    }
}
