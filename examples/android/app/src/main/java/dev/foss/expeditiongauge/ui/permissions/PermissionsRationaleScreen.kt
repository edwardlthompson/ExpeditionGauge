package dev.foss.expeditiongauge.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun PermissionsRationaleScreen(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GaugeBackHandler(onBack = onRequestPermissions)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.permissions_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.permissions_rationale),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.permissions_camera_stub),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(R.string.ble_permission_rationale),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("ble_permission_rationale"),
        )
        Button(
            onClick = onRequestPermissions,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("permissions_grant"),
        ) {
            Text(stringResource(R.string.permissions_grant))
        }
    }
}
