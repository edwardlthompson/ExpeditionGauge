package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun ActivityTypeFilterRow(
    selected: ActivityType?,
    onSelected: (ActivityType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = SpacingMd)
            .testTag("activity_filter_row"),
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text(stringResource(R.string.activity_filter_all)) },
            modifier = Modifier.testTag("activity_filter_ALL"),
        )
        ActivityType.entries.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelected(type) },
                label = { Text(activityTypeLabel(type)) },
                modifier = Modifier.testTag("activity_filter_${type.name}"),
            )
        }
    }
}

@Composable
fun activityTypeLabel(type: ActivityType): String = when (type) {
    ActivityType.DRIVE -> stringResource(R.string.activity_type_drive)
    ActivityType.OFFROAD -> stringResource(R.string.activity_type_offroad)
    ActivityType.TRACK -> stringResource(R.string.activity_type_track)
    ActivityType.TOWING -> stringResource(R.string.activity_type_towing)
    ActivityType.OTHER -> stringResource(R.string.activity_type_other)
}
