package dev.foss.expeditiongauge.about

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductUpdateTest {

    @Test
    fun dailyCheckWaitsAFullDay() {
        assertTrue(ProductUpdate.shouldCheckDaily(null, 0L))
        assertFalse(ProductUpdate.shouldCheckDaily(0L, ProductUpdate.MS_DAY - 1))
        assertTrue(ProductUpdate.shouldCheckDaily(0L, ProductUpdate.MS_DAY))
    }

    @Test
    fun assetVersionsComeFromProductFilenamesNotTags() {
        assertEquals("2.18.8", ProductUpdate.parseAssetVersion("ExpeditionGauge-2.18.8.apk"))
        assertEquals("2.19.0", ProductUpdate.parseAssetVersion("expeditiongauge-2.19.0-foss.apk"))
        assertEquals("2.19.0", ProductUpdate.parseAssetVersion("ExpeditionGauge-2.19.0-x64-setup.exe"))
        assertNull(ProductUpdate.parseAssetVersion("v0.22.1"))
        assertNull(ProductUpdate.parseAssetVersion("template-0.15.1"))
    }

    @Test
    fun isNewerThanCurrent() {
        assertTrue(ProductUpdate.isNewerVersion("2.18.8", "2.19.0"))
        assertFalse(ProductUpdate.isNewerVersion("2.19.0", "2.19.0"))
        assertFalse(ProductUpdate.isNewerVersion("2.19.0", "2.18.8"))
    }

    @Test
    fun selectProductAssetReadsInstallerFilename() {
        val picked = ProductUpdate.selectProductAsset(
            listOf(
                ProductUpdate.NamedAsset("sbom.cyclonedx.json", "https://example.com/sbom"),
                ProductUpdate.NamedAsset(
                    "ExpeditionGauge-2.19.0.apk",
                    "https://example.com/a.apk",
                ),
            ),
        )
        assertEquals("2.19.0", picked?.version)
        assertEquals("https://example.com/a.apk", picked?.url)
    }

    @Test
    fun donateNudgeOnlyAfterVersionChange() {
        assertFalse(ProductUpdate.shouldNudgeDonate(null, "2.18.8"))
        assertFalse(ProductUpdate.shouldNudgeDonate("2.18.8", "2.18.8"))
        assertTrue(ProductUpdate.shouldNudgeDonate("2.18.8", "2.19.0"))
    }

    @Test
    fun updatePromptSkipsDismissedOrEqualVersions() {
        assertTrue(ProductUpdate.shouldPromptUpdate("2.18.8", "2.19.0", null))
        assertFalse(ProductUpdate.shouldPromptUpdate("2.18.8", "2.19.0", "2.19.0"))
        assertFalse(ProductUpdate.shouldPromptUpdate("2.19.0", "2.19.0", null))
        assertFalse(ProductUpdate.shouldPromptUpdate("2.18.8", null, null))
    }
}
