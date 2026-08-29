package dev.foss.expeditiongauge.car.ui

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridge
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.R

/**
 * Primary AA screen: PaneTemplate large Drive HUD (Attitude | Telemetry | TPMS).
 *
 * Top: [APP_ICON] + ActionStrip Screenshot + Record/Stop.
 * Pane body: Zero (parked-only). No filler “Live OK” row — alert row only when needed;
 * otherwise a zero-width spacer (Pane requires ≥1 row).
 *
 * Bitmap is a square with a **3×1** cube strip letterboxed inside — AA hosts size
 * Pane images into a square and center-crop raw 3×1 bitmaps to the middle cube.
 */
class DrivePaneScreen(carContext: CarContext) : Screen(carContext) {

    private var lockedDisplaySpec: AaDisplaySpec? = null
    @Volatile private var lastTemplateInvalidateMs: Long = 0L

    init {
        val bridge = CarAppBridgeRegistry.bridge
        // Pane uses Screen.invalidate — keep ~2 Hz; Surface HUD refreshes faster elsewhere.
        bridge?.setInvalidationListener {
            val now = System.currentTimeMillis()
            if (now - lastTemplateInvalidateMs < PANE_INVALIDATE_MIN_MS) return@setInvalidationListener
            lastTemplateInvalidateMs = now
            invalidate()
        }
        bridge?.setToastHandler { message ->
            runCatching {
                CarToast.makeText(carContext, message, CarToast.LENGTH_SHORT).show()
            }
        }
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                CarAppBridgeRegistry.bridge?.onCarSessionStarted()
            }

            override fun onDestroy(owner: LifecycleOwner) {
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

    override fun onGetTemplate(): Template {
        return try {
            buildTemplate()
        } catch (t: Throwable) {
            Log.e(TAG, "onGetTemplate failed", t)
            DrivePaneTemplates.waitingTemplate("HUD error — reopen app")
        }
    }

    private fun buildTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")

        val spec = lockedDisplaySpec
            ?: DrivePaneTemplates.readDisplaySpec(carContext).also { lockedDisplaySpec = it }
        val hud = bridge.driveHud(spec)
        val paneBuilder = Pane.Builder().setImage(hud.image)
        DrivePaneTemplates.addRows(paneBuilder, hud.rows)
        paneBuilder.addAction(zeroAction(bridge))

        return PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(screenshotRecordStrip(bridge))
            .build()
    }

    private fun screenshotRecordStrip(bridge: CarAppBridge): ActionStrip {
        val screenshot = Action.Builder()
            .setIcon(resourceIcon(R.drawable.ic_aa_screenshot))
            .setOnClickListener { bridge.captureAaScreenshot() }
            .build()
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        val record = Action.Builder()
            .setTitle(TelemetryGridActions.recordTitle(bridge.isRecording()))
            .setIcon(resourceIcon(recordRes))
            .setOnClickListener {
                bridge.toggleRecord()
                invalidate()
            }
            .build()
        return ActionStrip.Builder()
            .addAction(screenshot)
            .addAction(record)
            .build()
    }

    private fun zeroAction(bridge: CarAppBridge): Action =
        Action.Builder()
            .setIcon(resourceIcon(R.drawable.ic_aa_zero))
            .setOnClickListener(
                ParkedOnlyOnClickListener.create {
                    if (!bridge.zeroAttitude()) {
                        runCatching {
                            CarToast.makeText(
                                carContext,
                                "Zero needs phone sensors",
                                CarToast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    invalidate()
                },
            )
            .build()

    private fun resourceIcon(drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(carContext, drawableRes)).build()

    companion object {
        private const val TAG = "DrivePaneScreen"
        private const val PANE_INVALIDATE_MIN_MS = 500L
    }
}
