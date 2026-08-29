package dev.foss.expeditiongauge.ui.theme

import dev.foss.expeditiongauge.keepawake.KeepAwakeMoving

internal fun shouldKeepScreenAwake(
    preferenceEnabled: Boolean,
    speedMps: Float?,
): Boolean = KeepAwakeMoving.shouldKeep(preferenceEnabled, speedMps)
