package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

/**
 * Vertical control rail sized to the cube gutter; icons shrink/grow with [iconSize].
 */
@Composable
fun DashboardHudSideChrome(
    recording: Boolean,
    isLive: Boolean,
    onMenuClick: () -> Unit,
    onRecordClick: () -> Unit,
    onMarkEvent: () -> Unit,
    onScreenshotClick: () -> Unit,
    iconSize: Dp = 40.dp,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        val btn = iconSize.coerceAtMost(maxWidth).coerceAtLeast(24.dp)
        val glyph = (btn.value * 0.62f).dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        ) {
            when {
                isLive -> Text(
                    text = stringResource(R.string.live_banner),
                    color = GaugeYellow,
                    style = MaterialTheme.typography.labelSmall,
                )
                recording -> Text(
                    text = stringResource(R.string.recording_live),
                    color = GaugeRed,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            GutterIconButton(
                onClick = onMenuClick,
                testTag = "dashboard_menu",
                buttonSize = btn,
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.dashboard_menu_open),
                tint = GaugeYellow,
                glyphSize = glyph,
            )
            GutterIconButton(
                onClick = onRecordClick,
                testTag = if (recording) "record_stop" else "record_play",
                buttonSize = btn,
                imageVector = if (recording) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (recording) R.string.recording_stop else R.string.recording_start,
                ),
                tint = if (recording) GaugeRed else GaugeYellow,
                glyphSize = glyph,
            )
            GutterIconButton(
                onClick = onScreenshotClick,
                testTag = "dashboard_screenshot",
                buttonSize = btn,
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = stringResource(R.string.dashboard_screenshot),
                tint = GaugeYellow,
                glyphSize = glyph,
            )
            if (recording && FeatureFlags.markEventEnabled) {
                GutterIconButton(
                    onClick = onMarkEvent,
                    testTag = "mark_event",
                    buttonSize = btn,
                    imageVector = Icons.Filled.Flag,
                    contentDescription = stringResource(R.string.mark_event_fab),
                    tint = GaugeYellow,
                    glyphSize = glyph,
                )
            }
        }
    }
}

@Composable
private fun GutterIconButton(
    onClick: () -> Unit,
    testTag: String,
    buttonSize: Dp,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: androidx.compose.ui.graphics.Color,
    glyphSize: Dp,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(buttonSize)
            .testTag(testTag),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(glyphSize),
        )
    }
}
