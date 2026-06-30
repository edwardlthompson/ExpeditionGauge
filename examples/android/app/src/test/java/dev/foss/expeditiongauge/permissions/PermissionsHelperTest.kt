package dev.foss.expeditiongauge.permissions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionsHelperTest {
    @Test
    fun optionalPermissionsIncludeCameraStub() {
        assertTrue(PermissionsHelper.optionalPermissions().contains(android.Manifest.permission.CAMERA))
    }

    @Test
    fun requiredPermissionsIncludeLocation() {
        assertTrue(
            PermissionsHelper.requiredPermissions().contains(android.Manifest.permission.ACCESS_FINE_LOCATION),
        )
    }

    @Test
    fun cameraNotInRequiredSet() {
        assertFalse(
            PermissionsHelper.requiredPermissions().contains(android.Manifest.permission.CAMERA),
        )
    }
}
