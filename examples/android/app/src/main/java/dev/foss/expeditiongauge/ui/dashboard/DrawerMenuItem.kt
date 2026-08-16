package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun DrawerMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    supportingText: String? = null,
    trailingChevron: Boolean = false,
    selected: Boolean = false,
    testTag: String? = null,
) {
    NavigationDrawerItem(
        label = {
            Column {
                Text(text = label, color = if (selected) GaugeYellow else GaugeScaleWhite)
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = GaugeScaleWhite,
                    )
                }
            }
        },
        selected = selected,
        onClick = onClick,
        icon = icon?.let { image ->
            {
                Icon(
                    imageVector = image,
                    contentDescription = null,
                    tint = GaugeYellow,
                )
            }
        },
        badge = if (trailingChevron) {
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.dashboard_menu_submenu),
                    tint = GaugeScaleWhite,
                )
            }
        } else {
            null
        },
        modifier = modifier
            .heightIn(min = 56.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedTextColor = GaugeScaleWhite,
            unselectedContainerColor = GaugeBackground,
            selectedContainerColor = GaugeBackground,
            selectedTextColor = GaugeYellow,
            selectedIconColor = GaugeYellow,
            unselectedIconColor = GaugeYellow,
        ),
    )
}
