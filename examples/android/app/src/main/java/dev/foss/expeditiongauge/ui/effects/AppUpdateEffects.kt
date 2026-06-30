package dev.foss.expeditiongauge.ui.effects

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.CheckSchedule
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.about.ReleaseAssetSelector
import dev.foss.expeditiongauge.about.ReleaseTagFetcher
import dev.foss.expeditiongauge.about.UpdateStatusEvaluator
import dev.foss.expeditiongauge.ExpeditionGaugeServices

@Composable
fun AppUpdateEffects(
    context: Context,
    appVersion: String,
    appUpdatePreferences: AppUpdatePreferences,
    services: ExpeditionGaugeServices,
    logInterval: Long,
    checkInterval: String,
    lastChecked: Long?,
    isOnline: Boolean,
    installedFormat: String?,
    pendingRestart: Boolean,
    updateStatus: MutableState<String>,
    applyAsset: MutableState<ReleaseAsset?>,
) {
    LaunchedEffect(logInterval) {
        services.recordingWriter.setLogIntervalMs(logInterval)
    }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus.value = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(checkInterval, lastChecked, isOnline, installedFormat, pendingRestart) {
        if (pendingRestart) return@LaunchedEffect
        if (!isOnline) return@LaunchedEffect
        if (!CheckSchedule.shouldCheck(checkInterval, lastChecked, System.currentTimeMillis())) return@LaunchedEffect
        val repo = ReleaseTagFetcher.loadReleaseRepo(context) ?: return@LaunchedEffect
        val release = ReleaseTagFetcher.fetchLatestRelease(repo) ?: return@LaunchedEffect
        val format = installedFormat ?: "apk"
        if (release.assets.isNotEmpty() && ReleaseAssetSelector.select(release.assets, format) == null) {
            updateStatus.value = context.getString(R.string.about_update_no_compatible)
            return@LaunchedEffect
        }
        appUpdatePreferences.setLastChecked(System.currentTimeMillis())
        val selected = ReleaseAssetSelector.select(release.assets, format)
        applyAsset.value = when (val result = UpdateStatusEvaluator.evaluate(appVersion, release.tag)) {
            is UpdateStatusEvaluator.Result.Current -> {
                updateStatus.value = context.getString(R.string.about_update_current)
                null
            }
            is UpdateStatusEvaluator.Result.Available -> {
                updateStatus.value = context.getString(R.string.about_update_available, result.version)
                selected
            }
        }
    }
}
