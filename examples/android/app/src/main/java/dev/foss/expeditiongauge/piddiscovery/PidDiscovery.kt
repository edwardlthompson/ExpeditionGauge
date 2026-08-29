package dev.foss.expeditiongauge.piddiscovery

import dev.foss.expeditiongauge.settings.ObdPidConfig

object PidDiscovery {
    fun applyToConfig(config: ObdPidConfig, pids: Set<Int>): ObdPidConfig =
        config.copy(
            rpm = 0x0C in pids,
            speed = 0x0D in pids,
            throttle = 0x11 in pids || 0x49 in pids,
            load = 0x04 in pids,
            voltage = 0x42 in pids,
            rearWheels = 0x5A in pids || 0x5B in pids,
        )

    fun summary(pids: Set<Int>): String? {
        if (pids.isEmpty()) return null
        return pids.sorted().joinToString(" ") { "%02X".format(it) }
    }
}
