package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.R

class TelemetryGridScreen(carContext: CarContext) : Screen(carContext) {

    /** Locked for the life of this screen; refreshed only via [refreshDisplaySpec]. */
    private var lockedDisplaySpec: AaDisplaySpec? = null

    init {
        val bridge = CarAppBridgeRegistry.bridge
        bridge?.setInvalidationListener { invalidate() }
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
        lockedDisplaySpec = TelemetryGridTemplates.readDisplaySpec(carContext)
    }

    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
            ?: return TelemetryGridTemplates.waitingTemplate("Open ExpeditionGauge on phone")

        val spec = lockedDisplaySpec
            ?: TelemetryGridTemplates.readDisplaySpec(carContext).also { lockedDisplaySpec = it }
        val tiles = bridge.hudTiles(spec)
        val listBuilder = ItemList.Builder()
            .addItem(TelemetryGridTemplates.gridItem(tiles.gMeter))
            .addItem(TelemetryGridTemplates.gridItem(tiles.telemetry))
        if (spec.maxGridItems >= 3) {
            listBuilder.addItem(TelemetryGridTemplates.gridItem(tiles.tpms))
        }

        val zeroIcon = resourceIcon(R.drawable.ic_aa_zero)
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        return GridTemplate.Builder()
            .setTitle("ExpeditionGauge")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .setActionStrip(
                TelemetryGridActions.build(
                    isRecording = bridge.isRecording(),
                    recordIcon = resourceIcon(recordRes),
                    zeroIcon = zeroIcon,
                    onRecordToggle = {
                        bridge.toggleRecord()
                        invalidate()
                    },
                    onZero = {
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
                ),
            )
            .build()
    }

    fun resolveDisplaySpec(): AaDisplaySpec =
        lockedDisplaySpec
            ?: TelemetryGridTemplates.readDisplaySpec(carContext).also { lockedDisplaySpec = it }

    private fun resourceIcon(drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(carContext, drawableRes)).build()
}
