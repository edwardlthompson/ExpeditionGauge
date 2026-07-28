package dev.foss.expeditiongauge.ble.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement

/** Pure exclusive MAC↔corner assign (one MAC per corner, one corner per MAC). */
object TpmsCornerAssignLogic {
    fun exclusiveAssign(
        current: Map<String, ImuPlacement>,
        macAddress: String,
        corner: ImuPlacement,
    ): Map<String, ImuPlacement> {
        val mac = macAddress.trim().uppercase()
        if (mac.isEmpty()) return current
        val next = current.toMutableMap()
        if (corner == ImuPlacement.Unassigned) {
            next.remove(mac)
            return next
        }
        next.entries.removeAll { it.value == corner && it.key != mac }
        next[mac] = corner
        return next
    }
}
