package dev.foss.expeditiongauge.ui.phonehuddtc

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun PhoneHudStatusLines(lines: List<String>, modifier: Modifier = Modifier) {
    if (lines.isEmpty()) return
    Column(modifier = modifier) {
        lines.forEach { PhoneHudStatusLine(it) }
    }
}

@Composable
fun PhoneHudStatusLine(line: String, modifier: Modifier = Modifier) {
    val im = line.startsWith("I/M")
    val spoken = stringResource(
        if (im) R.string.im_readiness_cd else R.string.obd_trip_cd,
        line,
    )
    Text(
        text = line,
        color = if (line.contains("not ready")) GaugeYellow else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .testTag(if (im) "im_readiness" else "obd_trip_since_clear")
            .semantics { contentDescription = spoken },
    )
}
