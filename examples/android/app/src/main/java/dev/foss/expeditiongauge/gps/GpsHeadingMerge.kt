package dev.foss.expeditiongauge.gps

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Apply GPS course-over-ground to HUD heading; hold last COG when the new sample is unknown. */
internal object GpsHeadingMerge {
    fun withCourse(
        current: TelemetrySnapshot,
        speedMps: Float,
        gpsCourseDeg: Float?,
        extras: TelemetrySnapshot.() -> TelemetrySnapshot,
    ): TelemetrySnapshot {
        val bodyYaw = current.bodyYawDeg ?: current.headingDeg
        val course = gpsCourseDeg ?: current.velocityHeadingDeg
        val displayHdg = GpsCourseLogic.displayHeadingDeg(bodyYaw, course, speedMps)
        return current.extras().copy(
            speedMps = speedMps,
            headingDeg = displayHdg,
            velocityHeadingDeg = course,
        )
    }
}
