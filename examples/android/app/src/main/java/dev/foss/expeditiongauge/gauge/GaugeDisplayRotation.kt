package dev.foss.expeditiongauge.gauge

/**
 * Remaps device-frame ball position to screen coordinates for [displayRotation].
 * Screen Y = longitudinal (front/back); screen X = lateral.
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

    /** Portrait HUD cube: rotate ball 90° clockwise so pitch is vertical, roll is lateral. */
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

    /**
     * Square HUD cube remap after [rotateBall].
     * Portrait: 90° CW. Landscape: 90° CCW at ROTATION_90, 90° CW at ROTATION_270.
     */
    fun applyHudCubeRemap(
        position: BallPosition,
        isPortraitLayout: Boolean,
        displayRotation: Int,
    ): BallPosition = when {
        isPortraitLayout -> rotate90Clockwise(position)
        displayRotation.mod(4) == 1 -> rotate90CounterClockwise(position)
        displayRotation.mod(4) == 3 -> rotate90Clockwise(position)
        else -> position
    }

    fun mapGForce(
        latG: Float,
        lonG: Float,
        displayRotation: Int,
        isPortraitLayout: Boolean = false,
    ): BallPosition {
        val mapped = rotateBall(GForceBallLogic.mapLatLonG(latG, lonG), displayRotation)
        return applyHudCubeRemap(mapped, isPortraitLayout, displayRotation)
    }

    fun mapAttitude(
        pitchDeg: Float,
        rollDeg: Float,
        displayRotation: Int,
        isPortraitLayout: Boolean = false,
    ): BallPosition {
        val mapped = rotateBall(AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg), displayRotation)
        return applyHudCubeRemap(mapped, isPortraitLayout, displayRotation)
    }
}
