package dev.foss.expeditiongauge.flyover

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

data class FlyoverMediaMarker(
    val sampleIndex: Int,
)

data class FlyoverRoutePoint(
    val x: Float,
    val y: Float,
    val color: Int,
)

object MapLibreFlyoverRenderer {
    const val STYLE_URI = "https://demotiles.maplibre.org/style.json"
    const val DEFAULT_WIDTH = 1280
    const val DEFAULT_HEIGHT = 720

    fun renderFrame(
        samples: List<SampleEntity>,
        keyframe: FlyoverKeyframe,
        width: Int = DEFAULT_WIDTH,
        height: Int = DEFAULT_HEIGHT,
        mediaMarkers: List<FlyoverMediaMarker> = emptyList(),
        enhancedOverlay: Boolean = true,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawSky(canvas, width, height)

        if (samples.isEmpty()) {
            FlyoverOverlay.draw(canvas, null, enhancedOverlay)
            return bitmap
        }

        val profile = FlyoverOverlay.buildElevationProfile(samples)
        val elevSpan = (profile.maxOrNull() ?: 0.0) - (profile.minOrNull() ?: 0.0)
        val routePoints = perspectiveRoute(
            samples = samples,
            upToIndex = keyframe.sampleIndex,
            bearingDeg = keyframe.bearingDeg,
            pitchDeg = keyframe.pitchDeg,
            profile = profile,
            elevSpan = max(elevSpan, 1.0),
            width = width,
            height = height,
        )
        drawRoute(canvas, routePoints)
        drawMediaMarkers(canvas, routePoints, mediaMarkers)
        FlyoverOverlay.draw(canvas, samples[keyframe.sampleIndex], enhancedOverlay)
        return bitmap
    }

    internal fun perspectiveRoute(
        samples: List<SampleEntity>,
        upToIndex: Int,
        bearingDeg: Double,
        pitchDeg: Double,
        profile: List<Double>,
        elevSpan: Double,
        width: Int,
        height: Int,
    ): List<FlyoverRoutePoint> {
        val slice = samples.take(upToIndex + 1)
        val coords = slice.mapNotNull { s ->
            val lat = s.latitude ?: return@mapNotNull null
            val lon = s.longitude ?: return@mapNotNull null
            lat to lon
        }
        if (coords.size < 2) return emptyList()

        val centerLat = coords.last().first
        val centerLon = coords.last().second
        val bearingRad = Math.toRadians(bearingDeg)
        val pitchFactor = sin(Math.toRadians(pitchDeg)).toFloat().coerceIn(0.2f, 1f)
        val horizonY = height * 0.35f

        return coords.mapIndexed { index, (lat, lon) ->
            val dx = (lon - centerLon) * cos(Math.toRadians(centerLat)) * 111_320.0
            val dy = (lat - centerLat) * 110_540.0
            val rotatedX = dx * cos(bearingRad) + dy * sin(bearingRad)
            val rotatedY = -dx * sin(bearingRad) + dy * cos(bearingRad)
            val depth = (1f / (1f + (rotatedY / 800.0).toFloat().coerceAtLeast(0.01f)))
            val x = (width * 0.5f + rotatedX.toFloat() * depth * 0.08f).coerceIn(0f, width.toFloat())
            val elevOffset = FlyoverOverlay.elevationOffset(slice[index], profile, index, elevSpan)
            val y = (horizonY + (height - horizonY) * (1f - depth * pitchFactor) - elevOffset * 80f * depth)
                .coerceIn(horizonY, height.toFloat())
            FlyoverRoutePoint(x, y, FlyoverOverlay.routeColorForSample(slice[index]))
        }
    }

    private fun drawSky(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                Color.rgb(30, 60, 120),
                Color.rgb(20, 24, 28),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun drawRoute(canvas: Canvas, points: List<FlyoverRoutePoint>) {
        if (points.size < 2) return
        var previous: FlyoverRoutePoint? = null
        points.forEach { point ->
            previous?.let { prev ->
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = point.color
                    strokeWidth = 5f
                    style = Paint.Style.STROKE
                }
                canvas.drawLine(prev.x, prev.y, point.x, point.y, paint)
            }
            previous = point
        }
    }

    private fun drawMediaMarkers(
        canvas: Canvas,
        routePoints: List<FlyoverRoutePoint>,
        markers: List<FlyoverMediaMarker>,
    ) {
        if (markers.isEmpty() || routePoints.isEmpty()) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }
        markers.forEach { marker ->
            routePoints.getOrNull(marker.sampleIndex)?.let { point ->
                canvas.drawCircle(point.x, point.y, 10f, paint)
            }
        }
    }
}
