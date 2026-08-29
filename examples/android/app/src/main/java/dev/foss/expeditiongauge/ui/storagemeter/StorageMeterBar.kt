package dev.foss.expeditiongauge.ui.storagemeter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.storagemeter.StorageMeter

@Composable
fun StorageMeterBar(
    usedBytes: Long,
    allowedBytes: Long,
    modifier: Modifier = Modifier,
) {
    val pct = StorageMeter.percentUsed(usedBytes, allowedBytes)
    val label = stringResource(R.string.storage_meter_cd, pct)
    LinearProgressIndicator(
        progress = { pct / 100f },
        modifier = modifier
            .fillMaxWidth()
            .testTag("storage_meter_bar")
            .semantics { contentDescription = label },
    )
}
