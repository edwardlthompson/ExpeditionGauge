package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.playback.SampleImuExtras
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.PlaybackDriftWedgeLeft
import dev.foss.expeditiongauge.ui.theme.PlaybackDriftWedgeRight
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun DriftAnalysisCanvas(
    sample: SampleEntity?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(SpacingMd)
            .semantics { contentDescription = "Drift analysis panel" },
    ) {
        Text(
            text = stringResource(R.string.playback_drift_analysis),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleSmall,
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(top = SpacingMd),
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val carW = size.width * 0.18f
            val carH = size.height * 0.35f
            val body = sample?.bodyYawDeg ?: sample?.headingDeg ?: 0f
            val velocity = sample?.velocityHeadingDeg ?: body
            val beta = sample?.driftAngleDeg ?: 0f

            drawRect(
                color = GaugeScaleWhite.copy(alpha = 0.25f),
                topLeft = Offset(center.x - carW / 2f, center.y - carH / 2f),
                size = androidx.compose.ui.geometry.Size(carW, carH),
                style = Stroke(width = 2f),
            )

            val headingRad = Math.toRadians((body - 90f).toDouble())
            drawLine(
                color = GaugeYellow,
                start = center,
                end = Offset(
                    center.x + (cos(headingRad) * carH).toFloat(),
                    center.y + (sin(headingRad) * carH).toFloat(),
                ),
                strokeWidth = 3f,
            )

            val velocityRad = Math.toRadians((velocity - 90f).toDouble())
            drawLine(
                color = Color.Cyan,
                start = center,
                end = Offset(
                    center.x + (cos(velocityRad) * carH * 0.9f).toFloat(),
                    center.y + (sin(velocityRad) * carH * 0.9f).toFloat(),
                ),
                strokeWidth = 2f,
            )

            val wedgeSweep = beta.coerceIn(-40f, 40f)
            if (kotlin.math.abs(wedgeSweep) > 1f) {
                val tail = min(size.width, size.height) * 0.25f
                val path = Path().apply {
                    moveTo(center.x, center.y)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            center.x - tail,
                            center.y - tail,
                            center.x + tail,
                            center.y + tail,
                        ),
                        startAngleDegrees = velocity - 90f,
                        sweepAngleDegrees = wedgeSweep,
                        forceMoveTo = false,
                    )
                    close()
                }
                drawPath(path, color = if (wedgeSweep > 0) PlaybackDriftWedgeLeft else PlaybackDriftWedgeRight)
            }

            SampleImuExtras.corners(sample?.extrasJson).forEach { corner ->
                val magnitude = (corner.latG ?: 0f).coerceIn(-2f, 2f)
                val vectorLen = magnitude * carW * 0.35f
                val cornerCenter = center + placementOffset(corner.placement, carW, carH)
                drawLine(
                    color = Color.Magenta,
                    start = cornerCenter,
                    end = cornerCenter + Offset(vectorLen, 0f),
                    strokeWidth = 2f,
                )
            }
        }
        Text(
            text = stringResource(R.string.playback_beta, sample?.driftAngleDeg ?: 0f),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(R.string.playback_body_yaw, sample?.bodyYawDeg ?: 0f),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(R.string.playback_velocity_hdg, sample?.velocityHeadingDeg ?: 0f),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(R.string.playback_slip_ratio, sample?.slipRatio ?: 0f),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun placementOffset(placement: String, carW: Float, carH: Float): Offset {
    val key = placement.lowercase()
    return when {
        "fl" in key || "front" in key && "left" in key -> Offset(-carW / 2f, -carH / 2f)
        "fr" in key || "front" in key && "right" in key -> Offset(carW / 2f, -carH / 2f)
        "rl" in key || "rear" in key && "left" in key -> Offset(-carW / 2f, carH / 2f)
        "rr" in key || "rear" in key && "right" in key -> Offset(carW / 2f, carH / 2f)
        else -> Offset.Zero
    }
}
