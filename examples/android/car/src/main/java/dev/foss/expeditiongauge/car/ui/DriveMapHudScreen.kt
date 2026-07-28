package dev.foss.expeditiongauge.car.ui

import android.util.Log
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.HudStripOrientation
import dev.foss.expeditiongauge.car.surface.DriveHudSurfaceCallback
import dev.foss.expeditiongauge.car.surface.DriveHudSurfacePainter

/**
 * Primary AA screen: full-bleed [NavigationTemplate] Surface painted with a native
 * Drive HUD (3×1 wide or 1×2 tall). Tap attitude cube to cycle modes (requires PAN).
 */
class DriveMapHudScreen(carContext: CarContext) : Screen(carContext) {

    private var lockedDisplaySpec: AaDisplaySpec? = null
    private val painter = DriveHudSurfacePainter()
    private val surfaceCallback = DriveHudSurfaceCallback(painter) {
        CarAppBridgeRegistry.bridge?.cycleAttitudeDisplay()
    }
    @Volatile private var surfaceLive = false
    @Volatile private var lastChromeRecording: Boolean? = null
    @Volatile private var lastChromeMuted: Boolean? = null
    @Volatile private var lastStripOrientation: HudStripOrientation? = null
    @Volatile private var loggedCubePx: Int = -1

    init {
        painter.onLayoutChanged = {
            val prevOrientation = lastStripOrientation
            pushHudToPainter()
            if (lastStripOrientation != prevOrientation) invalidate()
        }
        val bridge = CarAppBridgeRegistry.bridge
        bridge?.setInvalidationListener { onBridgeInvalidate() }
        bridge?.setToastHandler { message ->
            runCatching {
                CarToast.makeText(carContext, message, CarToast.LENGTH_SHORT).show()
            }
        }
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                CarAppBridgeRegistry.bridge?.onCarSessionStarted()
                runCatching {
                    carContext.getCarService(AppManager::class.java)
                        .setSurfaceCallback(surfaceCallback)
                    surfaceLive = true
                }.onFailure {
                    Log.w(TAG, "setSurfaceCallback failed", it)
                    surfaceLive = false
                }
                pushHudToPainter()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                runCatching {
                    carContext.getCarService(AppManager::class.java).setSurfaceCallback(null)
                }
                surfaceLive = false
                painter.onLayoutChanged = null
                val b = CarAppBridgeRegistry.bridge
                b?.onCarSessionStopped()
                b?.setInvalidationListener(null)
                b?.setToastHandler(null)
            }
        })
    }

    fun refreshDisplaySpec() {
        lockedDisplaySpec = DrivePaneTemplates.readDisplaySpec(carContext)
    }

    fun snapshotSurfaceFrame() = painter.snapshotFrame()

    override fun onGetTemplate(): Template {
        return try {
            val bridge = CarAppBridgeRegistry.bridge
                ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
            pushHudToPainter()
            DriveMapHudTemplates.navigation(
                carContext, bridge, painter.stripOrientation(),
            ) { invalidate() }
        } catch (t: Throwable) {
            Log.e(TAG, "NavigationTemplate failed — Pane fallback", t)
            paneFallbackTemplate()
        }
    }

    private fun paneFallbackTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
        return DriveMapHudTemplates.paneFallback(
            carContext, bridge, resolveSpec(),
        ) { invalidate() }
    }

    private fun onBridgeInvalidate() {
        pushHudToPainter()
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val recording = bridge.isRecording()
        val muted = bridge.isAlertsMuted()
        val orientation = painter.stripOrientation()
        val chromeChanged = if (orientation == HudStripOrientation.COLUMN) {
            lastChromeMuted != muted
        } else {
            lastChromeRecording != recording || lastChromeMuted != muted
        }
        if (chromeChanged) {
            lastChromeRecording = recording
            lastChromeMuted = muted
            invalidate()
        }
    }

    private fun pushHudToPainter() {
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val spec = resolveSpec()
        val orientation = painter.stripOrientation()
        lastStripOrientation = orientation
        val cubePx = painter.targetCubePx()
        if (cubePx != loggedCubePx) {
            loggedCubePx = cubePx
            Log.d(TAG, "Surface HUD cubePx=$cubePx orientation=$orientation")
        }
        val bmp = bridge.driveHudBitmap(
            displaySpec = spec,
            cubePxOverride = cubePx,
            orientation = orientation,
        )
        painter.setHudBitmap(bmp, spec.isDarkMode)
        if (surfaceLive) painter.requestDraw(force = true)
    }

    private fun resolveSpec(): AaDisplaySpec =
        lockedDisplaySpec
            ?: DrivePaneTemplates.readDisplaySpec(carContext).also { lockedDisplaySpec = it }

    companion object {
        private const val TAG = "DriveMapHudScreen"
        const val TITLE_SCREENSHOT = DriveMapHudChrome.TITLE_SCREENSHOT
        const val TITLE_LEVEL = DriveMapHudChrome.TITLE_LEVEL
    }
}
