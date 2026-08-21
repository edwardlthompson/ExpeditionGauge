package dev.foss.expeditiongauge.about

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ReleaseTagFetcherTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun loadReleaseRepoFromSyncedAssets() {
        assertEquals("edwardlthompson/ExpeditionGauge", ReleaseTagFetcher.loadReleaseRepo(context))
    }

    @Test
    fun manifestDeclaresInternetPermission() {
        val info = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS,
        )
        val perms = info.requestedPermissions?.toList() ?: emptyList()
        assertTrue(
            "INTERNET permission required for GitHub release fetch",
            perms.contains("android.permission.INTERNET"),
        )
    }

    @Test
    fun parseLatestReleaseReadsAssetNames() {
        val parsed = ReleaseTagFetcher.parseLatestRelease(
            """
            {
              "tag_name": "v0.15.1",
              "html_url": "https://github.com/edwardlthompson/ExpeditionGauge/releases/tag/v2.19.0",
              "assets": [
                {"name": "sbom.cyclonedx.json", "browser_download_url": "https://example.com/sbom"},
                {"name": "ExpeditionGauge-2.19.0.apk", "browser_download_url": "https://example.com/a.apk"}
              ]
            }
            """.trimIndent(),
        )
        assertEquals("v0.15.1", parsed?.tag)
        assertEquals("ExpeditionGauge-2.19.0.apk", parsed?.assets?.last()?.name)
        assertEquals("2.19.0", parsed?.assets?.last()?.let { ProductUpdate.parseAssetVersion(it.name) })
    }

    @Test
    fun fetchLatestReleaseReturnsNullForInvalidRepo() {
        val result = kotlinx.coroutines.runBlocking {
            ReleaseTagFetcher.fetchLatestRelease("invalid/empty-repo-404")
        }
        assertNull(result)
    }
}
