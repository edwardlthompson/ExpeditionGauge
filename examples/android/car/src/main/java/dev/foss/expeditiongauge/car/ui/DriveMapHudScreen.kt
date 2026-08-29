package dev.foss.expeditiongauge.car.ui

import android.util.Log
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.HudStripOrientation
import dev.foss.expeditiongauge.car.aacanvas.AaCustomCanvas
import dev.foss.expeditiongauge.car.surface.DriveHudSurfaceCallback
import dev.foss.expeditiongauge.car.surface.DriveHudSurfacePainter

/**
 * Primary AA screen: full-bleed NavigationTemplate Surface painted with a native
 * Drive HUD (3×1 wide or 1×2 tall). Tap attitude cube to cycle modes (requires PAN).
 */
class DriveMapHudScreen(carContext: CarContext) : Screen(carContext) {

    private val painter = DriveHudSurfacePainter()
    private val hudPush = DriveMapHudPush(carContext, painter)
    private val surfaceCallback = DriveHudSurfaceCallback(
        painter,
        onAttitudeTap = { CarAppBridgeRegistry.bridge?.cycleAttitudeDisplay() },
        onDtcFooterTap = { DriveMapHudParked.openDtc(carContext, screenManager) },
        onTelemetryTap = { DriveMapHudParked.openLibrary(carContext, screenManager) },
    )
    @Volatile private var surfaceLive = false
    @Volatile private var surfaceState = AaCustomCanvas.SurfaceState.PENDING
    @Volatile private var lastChromeRecording: Boolean? = null
    @Volatile private var lastChromeMuted: Boolean? = null

    init {
        painter.onLayoutChanged = {
            val prevOrientation = hudPush.lastStripOrientation
            hudPush.push(surfaceLive)
            if (hudPush.lastStripOrientation != prevOrientation) invalidate()
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
                    surfaceState = AaCustomCanvas.SurfaceState.LIVE
                }.onFailure {
                    Log.w(TAG, "setSurfaceCallback failed", it)
                    surfaceLive = false
                    surfaceState = AaCustomCanvas.SurfaceState.FAILED
                }
                hudPush.push(surfaceLive)
                if (AaCustomCanvas.kind(surfaceState) == AaCustomCanvas.Kind.PANE) invalidate()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                runCatching {
                    carContext.getCarService(AppManager::class.java).setSurfaceCallback(null)
                }
                surfaceLive = false
                surfaceState = AaCustomCanvas.SurfaceState.PENDING
                painter.onLayoutChanged = null
                val b = CarAppBridgeRegistry.bridge
                b?.onCarSessionStopped()
                b?.setInvalidationListener(null)
                b?.setToastHandler(null)
            }
        })
    }

    fun refreshDisplaySpec() {
        hudPush.refreshDisplaySpec()
    }

    fun snapshotSurfaceFrame() = painter.snapshotFrame()

    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
        if (AaCustomCanvas.kind(surfaceState) == AaCustomCanvas.Kind.PANE) {
            return paneFallbackTemplate()
        }
        return try {
            hudPush.push(surfaceLive)
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
            carContext, bridge, hudPush.resolveSpec(),
        ) { invalidate() }
    }

    private fun onBridgeInvalidate() {
        hudPush.push(surfaceLive)
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

    companion object {
        private const val TAG = "DriveMapHudScreen"
        const val TITLE_SCREENSHOT = DriveMapHudChrome.TITLE_SCREENSHOT
        const val TITLE_LEVEL = DriveMapHudChrome.TITLE_LEVEL
    }
}
