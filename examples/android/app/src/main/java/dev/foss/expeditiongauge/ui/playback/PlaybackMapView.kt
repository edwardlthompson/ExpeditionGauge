package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.drivingline.DrivingLineAnalysis
import dev.foss.expeditiongauge.drivingline.DrivingLineGeoJsonBuilder
import dev.foss.expeditiongauge.playback.DriftRouteStyling
import dev.foss.expeditiongauge.playback.HeatmapMetric
import dev.foss.expeditiongauge.playback.RouteGeoJsonBuilder
import dev.foss.expeditiongauge.ui.theme.PlaybackHeatmapHigh
import dev.foss.expeditiongauge.ui.theme.PlaybackHeatmapLow
import dev.foss.expeditiongauge.ui.theme.PlaybackHeatmapMid
import dev.foss.expeditiongauge.ui.theme.PlaybackHeatmapNeutral
import dev.foss.expeditiongauge.ui.theme.PlaybackMapGhostRoute
import dev.foss.expeditiongauge.ui.theme.PlaybackMapRouteCasing
import dev.foss.expeditiongauge.ui.theme.PlaybackMarkerApex
import dev.foss.expeditiongauge.ui.theme.PlaybackMarkerBrake
import dev.foss.expeditiongauge.ui.theme.PlaybackOffsetHigh
import dev.foss.expeditiongauge.ui.theme.PlaybackOffsetLow
import dev.foss.expeditiongauge.ui.theme.PlaybackOffsetMid
import dev.foss.expeditiongauge.ui.theme.PlaybackOffsetNeutral
import dev.foss.expeditiongauge.ui.theme.PlaybackSectorBoundary
import kotlin.time.Duration.Companion.milliseconds
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

@Composable
fun PlaybackMapView(
    samples: List<SampleEntity>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    heatmapEnabled: Boolean = false,
    heatmapMetric: HeatmapMetric = HeatmapMetric.DRIFT_ANGLE,
    showRoute: Boolean = true,
    showDrivingLine: Boolean = false,
    drivingLine: DrivingLineAnalysis? = null,
    showGhost: Boolean = false,
    ghostSamples: List<SampleEntity> = emptyList(),
    sectorLinesGeoJson: String? = null,
    showSectorBoundaries: Boolean = false,
) {
    val bounds = remember(samples, ghostSamples) {
        RouteGeoJsonBuilder.bounds(samples + ghostSamples)
    }
    val routeJson = remember(samples) { RouteGeoJsonBuilder.buildRouteGeoJson(samples) }
    val slipJson = remember(samples) { RouteGeoJsonBuilder.buildSlipGeoJson(samples) }
    val heatmapJson = remember(samples, heatmapMetric, heatmapEnabled) {
        if (heatmapEnabled) RouteGeoJsonBuilder.buildHeatmapGeoJson(samples, heatmapMetric) else ""
    }
    val ghostJson = remember(ghostSamples, showGhost) {
        if (showGhost && ghostSamples.isNotEmpty()) {
            RouteGeoJsonBuilder.buildGhostRouteGeoJson(ghostSamples)
        } else {
            """{"type":"FeatureCollection","features":[]}"""
        }
    }
    val drivingMarkersJson = remember(drivingLine, showDrivingLine) {
        if (showDrivingLine && drivingLine != null) {
            DrivingLineGeoJsonBuilder.buildMarkersGeoJson(drivingLine)
        } else {
            """{"type":"FeatureCollection","features":[]}"""
        }
    }
    val drivingBandsJson = remember(drivingLine, samples, showDrivingLine) {
        if (showDrivingLine && drivingLine != null) {
            DrivingLineGeoJsonBuilder.buildOffsetBandsGeoJson(drivingLine, samples)
        } else {
            """{"type":"FeatureCollection","features":[]}"""
        }
    }
    val sectorJson = remember(sectorLinesGeoJson, showSectorBoundaries) {
        if (showSectorBoundaries) {
            DrivingLineGeoJsonBuilder.buildSectorBoundariesGeoJson(sectorLinesGeoJson)
        } else {
            """{"type":"FeatureCollection","features":[]}"""
        }
    }
    val currentSample = samples.getOrNull(currentIndex)
    val vehicleJson = remember(currentSample) { RouteGeoJsonBuilder.buildVehicleGeoJson(currentSample) }

    val firstPosition = remember(bounds) {
        if (!bounds.isValid) {
            CameraPosition(target = Position(latitude = 0.0, longitude = 0.0), zoom = 2.0)
        } else {
            val centerLat = (bounds.minLat + bounds.maxLat) / 2.0
            val centerLon = (bounds.minLon + bounds.maxLon) / 2.0
            CameraPosition(target = Position(latitude = centerLat, longitude = centerLon), zoom = 14.0)
        }
    }

    val camera = rememberCameraState(firstPosition = firstPosition)

    LaunchedEffect(bounds) {
        if (!bounds.isValid) return@LaunchedEffect
        camera.animateTo(
            boundingBox = BoundingBox(
                southwest = Position(latitude = bounds.minLat, longitude = bounds.minLon),
                northeast = Position(latitude = bounds.maxLat, longitude = bounds.maxLon),
            ),
            duration = 400.milliseconds,
        )
    }

    LaunchedEffect(currentSample?.latitude, currentSample?.longitude, currentSample?.bodyYawDeg) {
        val lat = currentSample?.latitude ?: return@LaunchedEffect
        val lon = currentSample.longitude ?: return@LaunchedEffect
        val bearing = (currentSample.bodyYawDeg ?: currentSample.headingDeg).toDouble()
        camera.animateTo(
            finalPosition = camera.position.copy(
                target = Position(latitude = lat, longitude = lon),
                bearing = bearing,
                zoom = camera.position.zoom.coerceAtLeast(14.0),
            ),
            duration = 250.milliseconds,
        )
    }

    Box(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = BaseStyle.Uri("https://demotiles.maplibre.org/style.json"),
            cameraState = camera,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            val routeSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(routeJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val slipSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(slipJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val ghostSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(ghostJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val drivingMarkerSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(drivingMarkersJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val drivingBandSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(drivingBandsJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val sectorSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(sectorJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            val vehicleSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(vehicleJson),
                options = GeoJsonOptions(synchronousUpdate = true),
            )
            if (showGhost) {
                LineLayer(
                    id = "ghost-route",
                    source = ghostSource,
                    color = const(PlaybackMapGhostRoute),
                    width = const(6.dp),
                    opacity = const(0.45f),
                )
            }
            if (showRoute) {
                LineLayer(
                    id = "route-casing",
                    source = routeSource,
                    color = const(PlaybackMapRouteCasing),
                    width = switch(
                        input = feature["widthBucket"].asNumber(),
                        case(label = 3.0, output = const(12.dp)),
                        case(label = 2.0, output = const(9.dp)),
                        case(label = 1.0, output = const(7.dp)),
                        fallback = const(5.dp),
                    ),
                    opacity = const(0.9f),
                )
                LineLayer(
                    id = "route-beta",
                    source = routeSource,
                    color = switch(
                        input = feature["colorBucket"].asNumber(),
                        case(label = DriftRouteStyling.LEFT_BUCKET.toDouble(), output = const(DriftRouteStyling.DriftLeft)),
                        case(label = DriftRouteStyling.RIGHT_BUCKET.toDouble(), output = const(DriftRouteStyling.DriftRight)),
                        case(label = DriftRouteStyling.BRAKE_BUCKET.toDouble(), output = const(DriftRouteStyling.LonAccelBrake)),
                        case(label = DriftRouteStyling.ACCEL_BUCKET.toDouble(), output = const(DriftRouteStyling.LonAccelAccel)),
                        fallback = const(DriftRouteStyling.DriftNeutral),
                    ),
                    width = switch(
                        input = feature["widthBucket"].asNumber(),
                        case(label = 3.0, output = const(10.dp)),
                        case(label = 2.0, output = const(7.dp)),
                        case(label = 1.0, output = const(5.dp)),
                        fallback = const(3.dp),
                    ),
                )
                LineLayer(
                    id = "route-slip",
                    source = slipSource,
                    color = const(DriftRouteStyling.SlipHighlight),
                    width = const(8.dp),
                    opacity = feature["slipAlpha"].asNumber(),
                )
            }
            if (heatmapEnabled && heatmapJson.isNotEmpty()) {
                val heatmapSource = rememberGeoJsonSource(
                    data = GeoJsonData.JsonString(heatmapJson),
                    options = GeoJsonOptions(synchronousUpdate = true),
                )
                LineLayer(
                    id = "route-heatmap",
                    source = heatmapSource,
                    color = switch(
                        input = feature["colorBucket"].asNumber(),
                        case(label = 3.0, output = const(PlaybackHeatmapHigh)),
                        case(label = 2.0, output = const(PlaybackHeatmapMid)),
                        case(label = 1.0, output = const(PlaybackHeatmapLow)),
                        fallback = const(PlaybackHeatmapNeutral),
                    ),
                    width = const(10.dp),
                    opacity = const(0.85f),
                )
            }
            if (showSectorBoundaries) {
                LineLayer(
                    id = "sector-boundaries",
                    source = sectorSource,
                    color = const(PlaybackSectorBoundary),
                    width = const(4.dp),
                    opacity = const(0.75f),
                )
            }
            if (showDrivingLine) {
                LineLayer(
                    id = "driving-offset-bands",
                    source = drivingBandSource,
                    color = switch(
                        input = feature["offsetBucket"].asNumber(),
                        case(label = 3.0, output = const(PlaybackOffsetHigh)),
                        case(label = 2.0, output = const(PlaybackOffsetMid)),
                        case(label = 1.0, output = const(PlaybackOffsetLow)),
                        fallback = const(PlaybackOffsetNeutral),
                    ),
                    width = const(8.dp),
                    opacity = const(0.8f),
                )
                CircleLayer(
                    id = "driving-markers",
                    source = drivingMarkerSource,
                    color = switch(
                        input = feature["markerType"].asString(),
                        case(label = "apex", output = const(PlaybackMarkerApex)),
                        case(label = "brake", output = const(PlaybackMarkerBrake)),
                        fallback = const(Color.White),
                    ),
                    radius = const(7.dp),
                    strokeColor = const(Color.Black),
                    strokeWidth = const(1.dp),
                )
            }
            CircleLayer(
                id = "vehicle-marker",
                source = vehicleSource,
                color = const(Color.White),
                radius = const(6.dp),
                strokeColor = const(Color.Black),
                strokeWidth = const(2.dp),
            )
        }
    }
}
