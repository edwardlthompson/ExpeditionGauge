package dev.foss.expeditiongauge.recording

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dev.foss.expeditiongauge.settings.SettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AutoRecordMonitor(
    private val context: Context,
    private val settingsPreferences: SettingsPreferences,
    private val recordingWriter: RecordingWriter,
    private val scope: CoroutineScope,
) {
    private var receiver: BroadcastReceiver? = null
    private var disconnectJob: Job? = null

    fun start() {
        if (receiver != null) return
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) ?: return
                val address = device.address ?: return
                when (intent.action) {
                    BluetoothDevice.ACTION_ACL_CONNECTED -> scope.launch { onDeviceConnected(address) }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> scope.launch { onDeviceDisconnected(address) }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        context.registerReceiver(receiver, filter)
        scope.launch { checkAlreadyConnectedDevices() }
    }

    fun stop() {
        disconnectJob?.cancel()
        receiver?.let { context.unregisterReceiver(it) }
        receiver = null
    }

    private suspend fun onDeviceConnected(address: String) {
        val enabled = settingsPreferences.autoRecordEnabled.first()
        if (!enabled) return
        val allowlist = settingsPreferences.autoRecordDeviceAddresses.first()
        if (address !in allowlist) return
        if (recordingWriter.recording.value) return
        recordingWriter.startRecording(autoRecordTriggerAddress = address, manualStart = false)
    }

    private suspend fun onDeviceDisconnected(address: String) {
        if (!recordingWriter.recording.value) return
        if (recordingWriter.autoRecordTriggerAddress != address) return
        disconnectJob?.cancel()
        disconnectJob = scope.launch {
            delay(DISCONNECT_DEBOUNCE_MS)
            if (recordingWriter.autoRecordTriggerAddress == address && recordingWriter.recording.value) {
                recordingWriter.stopRecording()
            }
        }
    }

    private suspend fun checkAlreadyConnectedDevices() {
        val enabled = settingsPreferences.autoRecordEnabled.first()
        if (!enabled) return
        val allowlist = settingsPreferences.autoRecordDeviceAddresses.first()
        if (allowlist.isEmpty()) return
        if (recordingWriter.recording.value) return
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
        @Suppress("MissingPermission")
        val bonded = adapter.bondedDevices.orEmpty()
        val connected = bonded.firstOrNull { it.address in allowlist } ?: return
        onDeviceConnected(connected.address)
    }

    companion object {
        const val DISCONNECT_DEBOUNCE_MS = 3_000L
    }
}
