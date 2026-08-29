package dev.foss.expeditiongauge.ui.phonehuddtc

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
fun PhoneHudImLine(line: String, modifier: Modifier = Modifier) {
    val spoken = stringResource(R.string.im_readiness_cd, line)
    Text(
        text = line,
        color = if (line.contains("not ready")) GaugeYellow else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .testTag("im_readiness")
            .semantics { contentDescription = spoken },
    )
}
