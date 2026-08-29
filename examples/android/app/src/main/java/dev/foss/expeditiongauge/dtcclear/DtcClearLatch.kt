package dev.foss.expeditiongauge.dtcclear

/** One-shot Mode 04 request from the HUD onto the OBD poll loop. */
class DtcClearLatch {
    @Volatile
    private var requested = false

    fun request() {
        requested = true
    }

    fun consume(): Boolean {
        if (!requested) return false
        requested = false
        return true
    }
}
