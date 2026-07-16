package dev.foss.expeditiongauge.car.ui

import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ParkedOnlyOnClickListener

/**
 * GridTemplate ActionStrip constraints ([ACTIONS_CONSTRAINTS_SIMPLE][androidx.car.app.model.constraints.ActionsConstraints]):
 * max 2 actions, max **1** with a non-empty custom title.
 *
 * Record/Stop keeps the title slot (plus icon); Zero is icon-only and parked-only.
 */
object TelemetryGridActions {
    const val TITLE_RECORD = "Record"
    const val TITLE_STOP = "Stop"

    fun recordTitle(isRecording: Boolean): String =
        if (isRecording) TITLE_STOP else TITLE_RECORD

    fun build(
        isRecording: Boolean,
        recordIcon: CarIcon,
        zeroIcon: CarIcon,
        onRecordToggle: () -> Unit,
        onZero: () -> Unit,
    ): ActionStrip {
        val recordAction = Action.Builder()
            .setTitle(recordTitle(isRecording))
            .setIcon(recordIcon)
            .setOnClickListener(onRecordToggle)
            .build()
        val zeroAction = Action.Builder()
            .setIcon(zeroIcon)
            .setOnClickListener(ParkedOnlyOnClickListener.create(onZero))
            .build()
        return ActionStrip.Builder()
            .addAction(recordAction)
            .addAction(zeroAction)
            .build()
    }

    fun titledActionCount(strip: ActionStrip): Int =
        strip.actions.count { action ->
            val title = action.title
            title != null && !title.toString().isBlank()
        }

    fun isZeroParkedOnly(strip: ActionStrip): Boolean {
        val zero = strip.actions.getOrNull(1) ?: return false
        // ParkedOnly wraps the listener; presence of icon and no title identifies Zero.
        return zero.title == null && zero.icon != null
    }
}
