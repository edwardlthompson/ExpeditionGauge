package dev.foss.expeditiongauge

import dev.foss.expeditiongauge.obd.ObdConnectionPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** OBD / TPMS / IMU preference ↔ manager bindings for [ExpeditionGaugeServices]. */
internal fun ExpeditionGaugeServices.bindSensorPreferenceFlows(scope: CoroutineScope) {
    scope.launch {
        settingsPreferences.obdDeviceAddress.distinctUntilChanged().collect { address ->
            if (address == null) {
                obdManager.disconnect()
            } else {
                obdManager.selectDevice(address)
                if (obdManager.phase.value != ObdConnectionPhase.Connected &&
                    obdManager.phase.value != ObdConnectionPhase.Connecting
                ) {
                    obdManager.connect()
                }
            }
        }
    }
    scope.launch {
        settingsPreferences.tpmsCornerMap.collect { map ->
            bleTpmsManager.restoreAssignments(map)
        }
    }
    bleTpmsManager.onAssignmentsChanged = { map ->
        scope.launch { settingsPreferences.setTpmsCornerMap(map) }
    }
    scope.launch {
        settingsPreferences.imuPlacementMap.collect { map ->
            bleImuManager.restorePlacements(map)
        }
    }
    bleImuManager.onPlacementsChanged = { map ->
        scope.launch { settingsPreferences.setImuPlacementMap(map) }
    }
    bleImuManager.onPreferredDevicesChanged = { addresses ->
        scope.launch { settingsPreferences.setImuPreferredDevices(addresses) }
    }
    scope.launch {
        settingsPreferences.imuPreferredDevices.collect { addresses ->
            if (addresses.isNotEmpty()) {
                bleImuManager.connectPreferred(addresses)
            }
        }
    }
}

private fun ExpeditionGaugeServices.startSensorsInternal() {
    phoneSensorProvider.start()
    fusedGpsProvider.startPhone()
    bleImuManager.startScan()
    if (FeatureFlags.tpmsEnabled) bleTpmsManager.startScan()
}

private fun ExpeditionGaugeServices.stopSensorsInternal() {
    phoneSensorProvider.stop()
    fusedGpsProvider.stopPhone()
    bleImuManager.stopScan()
    bleTpmsManager.stopScan()
}

private val sensorHolds = java.util.concurrent.ConcurrentHashMap<ExpeditionGaugeServices, SensorHold>()

private fun ExpeditionGaugeServices.sensorHold(): SensorHold =
    sensorHolds.getOrPut(this) {
        SensorHold(onStart = { startSensorsInternal() }, onStop = { stopSensorsInternal() })
    }

fun ExpeditionGaugeServices.acquireSensors() {
    sensorHold().acquire()
}

fun ExpeditionGaugeServices.releaseSensors() {
    sensorHold().release()
}

fun ExpeditionGaugeServices.sensorHoldCount(): Int = sensorHold().holdCount()

/** Restart providers while a hold is active (e.g. after permission grant). */
fun ExpeditionGaugeServices.refreshSensorsIfHeld() {
    if (sensorHold().holdCount() > 0) startSensorsInternal()
}
