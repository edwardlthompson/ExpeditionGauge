package dev.foss.expeditiongauge.ui.phonehuddtc

import android.os.SystemClock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
) {
    var nowMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(entries) {
        if (entries.isEmpty()) return@LaunchedEffect
        while (true) {
            nowMs = SystemClock.elapsedRealtime()
            delay(250)
        }
    }
    val line = PhoneHudDtc.line(entries, nowMs) ?: return
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
            .semantics { contentDescription = spoken },
    )
}
