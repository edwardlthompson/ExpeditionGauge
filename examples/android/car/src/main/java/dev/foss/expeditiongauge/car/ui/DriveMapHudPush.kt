package dev.foss.expeditiongauge.car.ui

import android.util.Log
import androidx.car.app.CarContext
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.HudStripOrientation
import dev.foss.expeditiongauge.car.surface.DriveHudSurfacePainter

internal class DriveMapHudPush(
    private val carContext: CarContext,
    private val painter: DriveHudSurfacePainter,
) {
    var lockedDisplaySpec: AaDisplaySpec? = null
    var lastStripOrientation: HudStripOrientation? = null
    private var loggedCubePx: Int = -1

    fun refreshDisplaySpec() {
        lockedDisplaySpec = DrivePaneTemplates.readDisplaySpec(carContext)
    }

    fun resolveSpec(): AaDisplaySpec =
        lockedDisplaySpec
            ?: DrivePaneTemplates.readDisplaySpec(carContext).also { lockedDisplaySpec = it }

    fun push(surfaceLive: Boolean) {
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val spec = resolveSpec()
        val orientation = painter.stripOrientation()
        lastStripOrientation = orientation
        val cubePx = painter.targetCubePx()
        if (cubePx != loggedCubePx) {
            loggedCubePx = cubePx
            Log.d(TAG, "Surface HUD cubePx=$cubePx orientation=$orientation")
        }
        painter.setHudBitmap(
            bridge.driveHudBitmap(
                displaySpec = spec,
                cubePxOverride = cubePx,
                orientation = orientation,
            ),
            spec.isDarkMode,
            spec.isHighContrast,
        )
        if (surfaceLive) painter.requestDraw(force = true)
    }

    private companion object {
        const val TAG = "DriveMapHudScreen"
    }
}
