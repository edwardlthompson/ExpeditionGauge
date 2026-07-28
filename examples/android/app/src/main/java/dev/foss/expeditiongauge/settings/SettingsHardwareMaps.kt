package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import dev.foss.expeditiongauge.ble.ImuPlacement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** TPMS / IMU corner-map and preferred-device flows + writers. */
internal class SettingsHardwareMaps(private val context: Context) {
    private val keys = SettingsPreferencesKeys

    val tpmsCornerMap: Flow<Map<String, ImuPlacement>> =
        context.settingsDataStore.data.map { DeviceCornerMapCodec.decode(it[keys.tpmsCornerMap]) }

    val imuPlacementMap: Flow<Map<String, ImuPlacement>> =
        context.settingsDataStore.data.map { DeviceCornerMapCodec.decode(it[keys.imuPlacementMap]) }

    val imuPreferredDevices: Flow<Set<String>> = context.settingsDataStore.data.map { prefs ->
        prefs[keys.imuPreferredDevices]
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()
    }

    suspend fun setTpmsCornerMap(map: Map<String, ImuPlacement>) {
        val encoded = DeviceCornerMapCodec.encode(map)
        context.settingsDataStore.edit {
            if (encoded.isBlank()) it.remove(keys.tpmsCornerMap) else it[keys.tpmsCornerMap] = encoded
        }
    }

    suspend fun setImuPlacementMap(map: Map<String, ImuPlacement>) {
        val encoded = DeviceCornerMapCodec.encode(map)
        context.settingsDataStore.edit {
            if (encoded.isBlank()) it.remove(keys.imuPlacementMap) else it[keys.imuPlacementMap] = encoded
        }
    }

    suspend fun setImuPreferredDevices(addresses: Set<String>) {
        context.settingsDataStore.edit {
            if (addresses.isEmpty()) {
                it.remove(keys.imuPreferredDevices)
            } else {
                it[keys.imuPreferredDevices] = addresses.joinToString(",")
            }
        }
    }

    suspend fun setTpmsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[keys.tpmsEnabled] = enabled }
    }

    suspend fun setPressureUnit(unit: PressureUnit) {
        context.settingsDataStore.edit {
            it[keys.pressureUnit] = if (unit == PressureUnit.PSI) "psi" else "kpa"
        }
    }

    suspend fun setTempUnit(unit: TempUnit) {
        context.settingsDataStore.edit {
            it[keys.tempUnit] = if (unit == TempUnit.FAHRENHEIT) "fahrenheit" else "celsius"
        }
    }
}
