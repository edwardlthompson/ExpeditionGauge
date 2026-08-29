package dev.foss.expeditiongauge.ui.phonehuddtc

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.phonehuddtc.PhoneHudDtc
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import kotlinx.coroutines.delay

@Composable
fun PhoneHudDtcFooter(
    entries: List<DtcEntry>,
    modifier: Modifier = Modifier,
    canClear: Boolean = false,
    onClear: () -> Unit = {},
    statusLines: List<String> = emptyList(),
) {
    var nowMs by remember { mutableLongStateOf(0L) }
    var detail by remember { mutableStateOf<DtcEntry?>(null) }
    var confirmClear by remember { mutableStateOf(false) }
    LaunchedEffect(entries) {
        if (entries.isEmpty()) return@LaunchedEffect
        while (true) {
            nowMs = SystemClock.elapsedRealtime()
            delay(250)
        }
    }
    val line = PhoneHudDtc.line(entries, nowMs)
    if (line == null) {
        if (statusLines.isEmpty()) return
        PhoneHudStatusLines(statusLines, modifier)
        return
    }
    val spoken = stringResource(R.string.phone_hud_dtc_cd, line)
    Text(
        text = line,
        color = GaugeRed,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .testTag("phone_hud_dtc")
            .clickable { detail = PhoneHudDtc.current(entries, nowMs) }
            .semantics { contentDescription = spoken },
    )
    PhoneHudStatusLines(statusLines)
    val shown = detail ?: return
    AlertDialog(
        onDismissRequest = { detail = null },
        title = { Text(shown.code) },
        text = {
            Column {
                Text(
                    text = PhoneHudDtc.fullTitle(shown),
                    modifier = Modifier.testTag("phone_hud_dtc_detail"),
                )
                shown.freezeSummary?.let { summary ->
                    Text(
                        text = stringResource(R.string.freeze_frame_label) + ": " + summary,
                        modifier = Modifier.testTag("freeze_frame"),
                    )
                }
                PhoneHudStatusLines(statusLines)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { detail = null },
                modifier = Modifier.testTag("phone_hud_dtc_detail_close"),
            ) { Text(stringResource(R.string.phone_hud_dtc_close)) }
        },
        dismissButton = {
            val clearCd = stringResource(
                if (canClear) R.string.dtc_clear_action else R.string.dtc_clear_need_park,
            )
            TextButton(
                onClick = { if (canClear) confirmClear = true },
                enabled = canClear,
                modifier = Modifier
                    .testTag("dtc_clear_action")
                    .semantics { contentDescription = clearCd },
            ) { Text(stringResource(R.string.dtc_clear_action)) }
        },
        modifier = Modifier.testTag("phone_hud_dtc_dialog"),
    )
    if (confirmClear) {
        PhoneHudDtcClearDialog(
            onConfirm = {
                confirmClear = false
                detail = null
                onClear()
            },
            onDismiss = { confirmClear = false },
        )
    }
}
