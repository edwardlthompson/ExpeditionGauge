package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.CarAppBridge
import dev.foss.expeditiongauge.car.R

/** ActionStrip builders for [DriveMapHudScreen] (Nav vs Pane host limits). */
internal object DriveMapHudChrome {
    const val TITLE_SCREENSHOT = "Screenshot"
    const val TITLE_LEVEL = "Level"

    fun navChromeStrip(carContext: CarContext, bridge: CarAppBridge, onRecordToggle: () -> Unit): ActionStrip {
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        val screenshot = Action.Builder()
            .setTitle(TITLE_SCREENSHOT)
            .setIcon(resourceIcon(carContext, R.drawable.ic_aa_screenshot))
            .setFlags(Action.FLAG_IS_PERSISTENT)
            .setOnClickListener { bridge.captureAaScreenshot() }
            .build()
        val record = Action.Builder()
            .setTitle(TelemetryGridActions.recordTitle(bridge.isRecording()))
            .setIcon(resourceIcon(carContext, recordRes))
            .setFlags(Action.FLAG_IS_PERSISTENT)
            .setOnClickListener {
                if (bridge.isRecording()) bridge.stopRecording() else bridge.startRecording()
                onRecordToggle()
            }
            .build()
        return ActionStrip.Builder()
            .addAction(screenshot)
            .addAction(record)
            .addAction(levelAction(carContext, bridge, titled = true, onDone = onRecordToggle))
            .build()
    }

    /** Pane SIMPLE strip: max one titled action (Record). */
    fun paneChromeStrip(carContext: CarContext, bridge: CarAppBridge, onRecordToggle: () -> Unit): ActionStrip {
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        val screenshot = Action.Builder()
            .setIcon(resourceIcon(carContext, R.drawable.ic_aa_screenshot))
            .setOnClickListener { bridge.captureAaScreenshot() }
            .build()
        val record = Action.Builder()
            .setTitle(TelemetryGridActions.recordTitle(bridge.isRecording()))
            .setIcon(resourceIcon(carContext, recordRes))
            .setOnClickListener {
                if (bridge.isRecording()) bridge.stopRecording() else bridge.startRecording()
                onRecordToggle()
            }
            .build()
        return ActionStrip.Builder()
            .addAction(screenshot)
            .addAction(record)
            .build()
    }

    fun levelAction(
        carContext: CarContext,
        bridge: CarAppBridge,
        titled: Boolean,
        onDone: () -> Unit,
    ): Action {
        val builder = Action.Builder()
            .setIcon(resourceIcon(carContext, R.drawable.ic_aa_zero))
            .setFlags(Action.FLAG_IS_PERSISTENT)
            .setOnClickListener(
                ParkedOnlyOnClickListener.create {
                    if (!bridge.zeroAttitude()) {
                        runCatching {
                            CarToast.makeText(
                                carContext,
                                "Level needs phone sensors",
                                CarToast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    onDone()
                },
            )
        if (titled) builder.setTitle(TITLE_LEVEL)
        return builder.build()
    }

    private fun resourceIcon(carContext: CarContext, drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(carContext, drawableRes)).build()
}
