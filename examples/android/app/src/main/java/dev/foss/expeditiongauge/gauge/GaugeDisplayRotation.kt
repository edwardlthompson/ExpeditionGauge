package dev.foss.expeditiongauge.gauge

/**
 * Remaps device-frame ball position to HUD screen coordinates.
 *
 * Locked contract: `docs/design/GMETER_HUD_ROTATION.md`
 *
 * Pipeline:
 * 1. Portrait layout: identity cube (roll→X, pitch→Y). Do not 90°-swap or mirror pitch —
 *    braking (−pitch) → ball toward front/top; accel (+pitch) → ball toward rear/bottom.
 * 2. [rotateBall] for [displayRotation] (legacy device-ball paths).
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

    /**
     * Portrait tile: keep vehicle axes — roll on screen X, pitch on screen Y.
     * Identity (no pitch mirror / no 90° swap): braking → front/top, accel → rear/bottom.
     */
    fun applyPortraitCubeRemap(position: BallPosition): BallPosition = position

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
    ): BallPosition {
        // Vehicle-frame telemetry — portrait cube only (no displayRotation ball remaps).
        @Suppress("UNUSED_PARAMETER")
        val unused = displayRotation
        val device = AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg)
        return if (isPortraitLayout) applyPortraitCubeRemap(device) else device
    }

    fun mapFusionToInclinometerAxes(
        pitchDeg: Float,
        rollDeg: Float,
        isPortraitLayout: Boolean = true,
        displayRotation: Int = 0,
        calibrationDisplayRotation: Int = 0,
    ): Pair<Float, Float> {
        @Suppress("UNUSED_PARAMETER")
        listOf(isPortraitLayout, displayRotation, calibrationDisplayRotation)
        return pitchDeg to rollDeg
    }

    fun unwrapPhoneRotation(pitchDeg: Float, rollDeg: Float, rotationDelta: Int) =
        PhoneRotationUnwrap.unwrapPhoneRotation(pitchDeg, rollDeg, rotationDelta)

    fun wrapSigned180(deg: Float) = PhoneRotationUnwrap.wrapSigned180(deg)

    /** Screen-relative pitch/roll via G-meter ball pipeline (legacy / tests). */
    fun mapAttitudeToScreenAxes(
        pitchDeg: Float,
        rollDeg: Float,
        displayRotation: Int,
        isPortraitLayout: Boolean,
    ): Pair<Float, Float> {
        val ball = mapAttitude(pitchDeg, rollDeg, displayRotation, isPortraitLayout)
        val max = AttitudeBallLogic.MAX_ANGLE_DEG
        // Ball X = roll, Y = pitch (portrait and landscape vehicle-frame).
        return (ball.normalizedY * max) to (ball.normalizedX * max)
    }
}
