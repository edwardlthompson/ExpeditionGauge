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
    const val TITLE_SCREENSHOT = "Capture"
    const val TITLE_LEVEL = "Level"
    const val TITLE_MUTE = "Mute"
    const val TITLE_UNMUTE = "Unmute"

    fun navChromeStrip(carContext: CarContext, bridge: CarAppBridge, onChromeChange: () -> Unit): ActionStrip {
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        return ActionStrip.Builder()
            .addAction(muteAction(carContext, bridge, titled = true, onDone = onChromeChange))
            .addAction(
                Action.Builder()
                    .setTitle(TITLE_SCREENSHOT)
                    .setIcon(resourceIcon(carContext, R.drawable.ic_aa_screenshot))
                    .setFlags(Action.FLAG_IS_PERSISTENT)
                    .setOnClickListener { bridge.captureAaScreenshot() }
                    .build(),
            )
            .addAction(
                Action.Builder()
                    .setTitle(TelemetryGridActions.recordTitle(bridge.isRecording()))
                    .setIcon(resourceIcon(carContext, recordRes))
                    .setFlags(Action.FLAG_IS_PERSISTENT)
                    .setOnClickListener {
                        bridge.toggleRecord()
                        onChromeChange()
                    }
                    .build(),
            )
            .addAction(levelAction(carContext, bridge, titled = true, onDone = onChromeChange))
            .build()
    }

    /**
     * Tall/COLUMN Surface: no Capture/Record/Level.
     * Hosts reject a fully empty ActionStrip (ANR / “unexpected error”); keep icon-only Mute.
     */
    fun columnChromeStrip(
        carContext: CarContext,
        bridge: CarAppBridge,
        onChromeChange: () -> Unit,
    ): ActionStrip = ActionStrip.Builder()
        .addAction(muteAction(carContext, bridge, titled = false, onDone = onChromeChange))
        .build()

    /** Pane SIMPLE strip: max one titled action (Record). Mute is icon-only. */
    fun paneChromeStrip(carContext: CarContext, bridge: CarAppBridge, onRecordToggle: () -> Unit): ActionStrip {
        val recordRes = if (bridge.isRecording()) R.drawable.ic_aa_stop else R.drawable.ic_aa_record
        return ActionStrip.Builder()
            .addAction(muteAction(carContext, bridge, titled = false, onDone = onRecordToggle))
            .addAction(
                Action.Builder()
                    .setIcon(resourceIcon(carContext, R.drawable.ic_aa_screenshot))
                    .setOnClickListener { bridge.captureAaScreenshot() }
                    .build(),
            )
            .addAction(
                Action.Builder()
                    .setTitle(TelemetryGridActions.recordTitle(bridge.isRecording()))
                    .setIcon(resourceIcon(carContext, recordRes))
                    .setOnClickListener {
                        bridge.toggleRecord()
                        onRecordToggle()
                    }
                    .build(),
            )
            .build()
    }

    fun muteAction(
        carContext: CarContext,
        bridge: CarAppBridge,
        titled: Boolean,
        onDone: () -> Unit,
    ): Action {
        val muted = bridge.isAlertsMuted()
        val builder = Action.Builder()
            .setIcon(
                resourceIcon(
                    carContext,
                    if (muted) R.drawable.ic_aa_mute else R.drawable.ic_aa_unmute,
                ),
            )
            .setFlags(Action.FLAG_IS_PERSISTENT)
            .setOnClickListener {
                bridge.toggleAlertsMuted()
                onDone()
            }
        if (titled) {
            builder.setTitle(if (muted) TITLE_UNMUTE else TITLE_MUTE)
        }
        return builder.build()
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
