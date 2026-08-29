package dev.foss.expeditiongauge.ui.shiftlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.LocalColorblindHud

@Composable
fun ShiftLightLamp(active: Boolean, modifier: Modifier = Modifier) {
    if (!active) return
    val spoken = stringResource(R.string.shift_light_cd)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(LocalColorblindHud.current.alertRed)
            .testTag("obd_shift_light")
            .semantics { contentDescription = spoken },
    )
}
