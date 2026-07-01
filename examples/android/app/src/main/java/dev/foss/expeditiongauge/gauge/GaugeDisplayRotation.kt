package dev.foss.expeditiongauge.gauge

/**
 * Remaps device-frame ball position to HUD screen coordinates.
 *
 * Locked contract: `docs/design/GMETER_HUD_ROTATION.md`
 *
 * Pipeline:
 * 1. Portrait layout: pitch mirror + 90° CW in **device** space (validated on OnePlus 12).
 * 2. [rotateBall] for [displayRotation] (all orientations).
 * 3. Landscape layout only: extra cube remap per rotation so pitch stays vertical on the tile.
 */
object GaugeDisplayRotation {
    fun rotateBall(position: BallPosition, displayRotation: Int): BallPosition {
        val (x, y) = when (displayRotation.mod(4)) {
            0 -> position.normalizedX to position.normalizedY
            1 -> -position.normalizedY to position.normalizedX
            2 -> -position.normalizedX to -position.normalizedY
            3 -> position.normalizedY to -position.normalizedX
            else -> position.normalizedX to position.normalizedY
        }
        return position.copy(
            normalizedX = x.coerceIn(-1f, 1f),
            normalizedY = y.coerceIn(-1f, 1f),
        )
    }

    /** 90° CW: (x′, y′) = (−y, x). Portrait HUD cube — do not replace with CCW or identity. */
    fun rotate90Clockwise(position: BallPosition): BallPosition =
        position.copy(
            normalizedX = (-position.normalizedY).coerceIn(-1f, 1f),
            normalizedY = position.normalizedX.coerceIn(-1f, 1f),
        )

    /** Inverse of [rotate90Clockwise]. */
    fun rotate90CounterClockwise(position: BallPosition): BallPosition =
        position.copy(
            normalizedX = position.normalizedY.coerceIn(-1f, 1f),
            normalizedY = (-position.normalizedX).coerceIn(-1f, 1f),
        )

    fun mirrorPitch(position: BallPosition): BallPosition =
        position.copy(normalizedY = (-position.normalizedY).coerceIn(-1f, 1f))

    /** Portrait tile: mirror pitch (device Y) then 90° CW — device space, before [rotateBall]. */
    fun applyPortraitCubeRemap(position: BallPosition): BallPosition =
        rotate90Clockwise(mirrorPitch(position))

    /** Landscape tile: extra remap after [rotateBall] so pitch stays on screen Y, roll on X. */
    fun applyLandscapePostRemap(position: BallPosition, displayRotation: Int): BallPosition =
        when (displayRotation.mod(4)) {
            1 -> rotate90CounterClockwise(position)
            2 -> position.copy(
                normalizedX = (-position.normalizedX).coerceIn(-1f, 1f),
                normalizedY = (-position.normalizedY).coerceIn(-1f, 1f),
            )
            3 -> rotate90Clockwise(position)
            else -> position
        }

    fun mapDeviceBallToHudScreen(
        device: BallPosition,
        displayRotation: Int,
        isPortraitLayout: Boolean,
    ): BallPosition {
        val cubeDevice = if (isPortraitLayout) applyPortraitCubeRemap(device) else device
        val onScreen = rotateBall(cubeDevice, displayRotation)
        return if (isPortraitLayout) onScreen else applyLandscapePostRemap(onScreen, displayRotation)
    }

    fun mapGForce(
        latG: Float,
        lonG: Float,
        displayRotation: Int,
        isPortraitLayout: Boolean = false,
    ): BallPosition = mapDeviceBallToHudScreen(
        GForceBallLogic.mapLatLonG(latG, lonG),
        displayRotation,
        isPortraitLayout,
    )

    fun mapAttitude(
        pitchDeg: Float,
        rollDeg: Float,
        displayRotation: Int,
        isPortraitLayout: Boolean = false,
    ): BallPosition = mapDeviceBallToHudScreen(
        AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg),
        displayRotation,
        isPortraitLayout,
    )
}
