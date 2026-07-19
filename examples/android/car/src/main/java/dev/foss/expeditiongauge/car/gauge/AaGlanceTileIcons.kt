package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.AaDisplaySpec
import java.util.concurrent.ConcurrentHashMap

/** Size-keyed pools for Telemetry / TPMS glance bitmaps (immutable copies for CarIcon). */
object AaGlanceTileIcons {
    private val telemetryPool = ConcurrentHashMap<Int, TelemetryTileBitmapRenderer>()
    private val tpmsPool = ConcurrentHashMap<Int, TpmsTileBitmapRenderer>()

    fun telemetry(
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): CarIcon {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_BITMAP_PX)
        val rendered = telemetryPool.getOrPut(clamped) { TelemetryTileBitmapRenderer(clamped) }
            .render(speedLabel, headingLabel, altLabel, darkBackground)
        return CarIcon.Builder(IconCompat.createWithBitmap(copyOf(rendered))).build()
    }

    fun tpms(
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): CarIcon {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_BITMAP_PX)
        val rendered = tpmsPool.getOrPut(clamped) { TpmsTileBitmapRenderer(clamped) }
            .render(fl, fr, rl, rr, darkBackground)
        return CarIcon.Builder(IconCompat.createWithBitmap(copyOf(rendered))).build()
    }

    fun renderTelemetryBitmap(
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): Bitmap {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_BITMAP_PX)
        return copyOf(
            telemetryPool.getOrPut(clamped) { TelemetryTileBitmapRenderer(clamped) }
                .render(speedLabel, headingLabel, altLabel, darkBackground),
        )
    }

    fun renderTpmsBitmap(
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): Bitmap {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_BITMAP_PX)
        return copyOf(
            tpmsPool.getOrPut(clamped) { TpmsTileBitmapRenderer(clamped) }
                .render(fl, fr, rl, rr, darkBackground),
        )
    }

    private fun copyOf(source: Bitmap): Bitmap = source.copy(Bitmap.Config.ARGB_8888, false)
}
