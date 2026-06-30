package dev.foss.expeditiongauge.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionsHelper {
    fun requiredPermissions(): Array<String> {
        val base = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            base += Manifest.permission.BLUETOOTH_SCAN
            base += Manifest.permission.BLUETOOTH_CONNECT
        }
        return base.toTypedArray()
    }

    fun hasAll(context: Context): Boolean =
        requiredPermissions().all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun request(activity: ComponentActivity, launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(requiredPermissions())
    }
}
