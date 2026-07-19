package dev.foss.expeditiongauge.car.ui

import android.util.Log
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.surface.DriveHudSurfaceCallback
import dev.foss.expeditiongauge.car.surface.DriveHudSurfacePainter

/**
 * Primary AA screen: full-bleed [NavigationTemplate] Surface painted with a native 3×1
 * Drive HUD. Tap left cube to cycle attitude modes (requires [Action.PAN] map strip).
 */
class DriveMapHudScreen(carContext: CarContext) : Screen(carContext) {

    private var lockedDisplaySpec: AaDisplaySpec? = null
    private val painter = DriveHudSurfacePainter()
    private val surfaceCallback = DriveHudSurfaceCallback(painter) {
        CarAppBridgeRegistry.bridge?.cycleAttitudeDisplay()
    }
    @Volatile private var surfaceLive = false
    @Volatile private var lastChromeRecording: Boolean? = null
    @Volatile private var loggedCubePx: Int = -1

    init {
        painter.onLayoutChanged = { pushHudToPainter() }
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
            buildNavTemplate()
        } catch (t: Throwable) {
            Log.e(TAG, "NavigationTemplate failed — Pane fallback", t)
            paneFallbackTemplate()
        }
    }

    private fun buildNavTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
        pushHudToPainter()
        return NavigationTemplate.Builder()
            .setMapActionStrip(ActionStrip.Builder().addAction(Action.PAN).build())
            .setActionStrip(DriveMapHudChrome.navChromeStrip(carContext, bridge) { invalidate() })
            .build()
    }

    private fun paneFallbackTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
        val spec = resolveSpec()
        val hud = bridge.driveHud(spec)
        val paneBuilder = androidx.car.app.model.Pane.Builder().setImage(hud.image)
        DrivePaneTemplates.addRows(paneBuilder, hud.rows)
        paneBuilder.addAction(
            DriveMapHudChrome.levelAction(carContext, bridge, titled = false) { invalidate() },
        )
        return androidx.car.app.model.PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(DriveMapHudChrome.paneChromeStrip(carContext, bridge) { invalidate() })
            .build()
    }

    private fun onBridgeInvalidate() {
        pushHudToPainter()
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val recording = bridge.isRecording()
        if (lastChromeRecording != recording) {
            lastChromeRecording = recording
            invalidate()
        }
    }

    private fun pushHudToPainter() {
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val spec = resolveSpec()
        val cubePx = painter.targetCubePx()
        if (cubePx != loggedCubePx) {
            loggedCubePx = cubePx
            Log.d(TAG, "Surface HUD cubePx=$cubePx")
        }
        val bmp = bridge.driveHudBitmap(spec, cubePxOverride = cubePx)
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
