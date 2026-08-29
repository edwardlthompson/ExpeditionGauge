package dev.foss.expeditiongauge.blepermissionrationale

/** Polished BLE scan rationale — pairing only, never background tracking. */
object BlePermissionRationale {
    const val SCAN =
        "Nearby devices are scanned only to pair IMU, TPMS, OBD, and external GPS. Nothing is uploaded."

    fun forScan(): String = SCAN
}
