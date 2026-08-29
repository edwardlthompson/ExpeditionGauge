package dev.foss.expeditiongauge.ui.timing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.timing.TrackLineBuilder
import dev.foss.expeditiongauge.trackautodetect.TrackAutodetect
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.launch

@Composable
fun TrackSetupScreen(
    telemetryBus: TelemetryBus,
    settingsPreferences: SettingsPreferences,
    onBack: () -> Unit,
    sampleLoader: suspend () -> List<SampleEntity> = { emptyList() },
    modifier: Modifier = Modifier,
) {
    val snapshot by telemetryBus.snapshots.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.telemetry.TelemetrySnapshot.empty(),
    )
    val startFinish by settingsPreferences.trackStartFinishGeoJson.collectAsStateWithLifecycle(initialValue = null)
    val sectors by settingsPreferences.trackSectorLinesGeoJson.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    var detectFailed by remember { mutableStateOf(false) }
    val hasGps = snapshot.latitude != null && snapshot.longitude != null

    GaugeBackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.track_setup_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Text(
            text = stringResource(R.string.track_setup_hint),
            style = MaterialTheme.typography.bodySmall,
            color = GaugeScaleWhite,
        )
        Text(
            text = if (startFinish != null) {
                stringResource(R.string.track_setup_start_finish_set)
            } else {
                stringResource(R.string.track_setup_start_finish_missing)
            },
            color = if (startFinish != null) GaugeYellow else GaugeScaleWhite,
        )
        Text(
            text = stringResource(R.string.track_setup_sector_count, TrackLineBuilder.sectorCount(sectors)),
            color = GaugeScaleWhite,
        )
        Button(
            onClick = {
                val lat = snapshot.latitude ?: return@Button
                val lon = snapshot.longitude ?: return@Button
                val line = TrackLineBuilder.perpendicularLine(lat, lon, snapshot.headingDeg)
                scope.launch {
                    settingsPreferences.setTrackStartFinishGeoJson(
                        TrackLineBuilder.toStartFinishGeoJson(line),
                    )
                }
            },
            enabled = hasGps,
            modifier = Modifier.fillMaxWidth().testTag("track_set_start_finish"),
        ) {
            Text(stringResource(R.string.track_setup_set_start_finish))
        }
        Button(
            onClick = {
                val lat = snapshot.latitude ?: return@Button
                val lon = snapshot.longitude ?: return@Button
                val line = TrackLineBuilder.perpendicularLine(lat, lon, snapshot.headingDeg)
                scope.launch {
                    settingsPreferences.setTrackSectorLinesGeoJson(
                        TrackLineBuilder.appendSectorLine(sectors, line),
                    )
                }
            },
            enabled = hasGps && TrackLineBuilder.sectorCount(sectors) < 9,
            modifier = Modifier.fillMaxWidth().testTag("track_add_sector"),
        ) {
            Text(stringResource(R.string.track_setup_add_sector))
        }
        Button(
            onClick = {
                scope.launch {
                    val geo = TrackAutodetect.startFinishGeoJson(sampleLoader())
                    detectFailed = geo == null
                    if (geo != null) settingsPreferences.setTrackStartFinishGeoJson(geo)
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("track_autodetect"),
        ) {
            Text(stringResource(R.string.track_setup_autodetect))
        }
        if (detectFailed) {
            Text(
                text = stringResource(R.string.track_setup_autodetect_failed),
                color = GaugeScaleWhite,
                modifier = Modifier.testTag("track_autodetect_failed"),
            )
        }
        Button(
            onClick = { scope.launch { settingsPreferences.clearTrackConfig() } },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.track_setup_clear))
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
