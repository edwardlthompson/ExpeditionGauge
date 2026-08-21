package dev.foss.expeditiongauge.ui.effects

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.DonateLinks
import dev.foss.expeditiongauge.about.ProductUpdate
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.about.ReleaseTagFetcher
import dev.foss.expeditiongauge.about.UpdateStatusEvaluator
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.ui.about.DonateNudgeDialog
import dev.foss.expeditiongauge.ui.about.UpdateAvailableDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private sealed class LaunchPrompt {
    data object Donate : LaunchPrompt()
    data class Update(val version: String, val url: String) : LaunchPrompt()
}

@Composable
fun LaunchPromptEffects(
    enabled: Boolean,
    context: Context,
    appVersion: String,
    appUpdatePreferences: AppUpdatePreferences,
    services: ExpeditionGaugeServices,
    logInterval: Long,
    pendingRestart: Boolean,
    updateStatus: MutableState<String>,
    applyAsset: MutableState<ReleaseAsset?>,
) {
    var prompt by remember { mutableStateOf<LaunchPrompt?>(null) }
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(logInterval) {
        services.recordingWriter.setLogIntervalMs(logInterval)
    }
    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus.value = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(enabled, pendingRestart) {
        if (!enabled || pendingRestart) return@LaunchedEffect
        try {
            val lastSeen = appUpdatePreferences.lastSeenVersion.first()
            if (ProductUpdate.shouldNudgeDonate(lastSeen, appVersion)) {
                prompt = LaunchPrompt.Donate
                return@LaunchedEffect
            }
            appUpdatePreferences.markVersionSeen(appVersion)
            val interval = appUpdatePreferences.checkInterval.first()
            if (!SettingsLogic.isUpdateCheckEnabled(interval)) return@LaunchedEffect
            val lastCheck = appUpdatePreferences.lastChecked.first()
            val now = System.currentTimeMillis()
            if (!ProductUpdate.shouldCheckDaily(lastCheck, now)) return@LaunchedEffect
            val repo = ReleaseTagFetcher.loadReleaseRepo(context) ?: return@LaunchedEffect
            val release = ReleaseTagFetcher.fetchLatestRelease(repo)
            appUpdatePreferences.markChecked(now)
            if (release == null || release.assets.isEmpty()) return@LaunchedEffect
            val named = release.assets.map { ProductUpdate.NamedAsset(it.name, it.url) }
            val product = ProductUpdate.selectProductAsset(named) ?: return@LaunchedEffect
            val dismissed = appUpdatePreferences.dismissedVersion.first()
            when (val result = UpdateStatusEvaluator.evaluate(appVersion, product.version)) {
                is UpdateStatusEvaluator.Result.Current -> {
                    updateStatus.value = context.getString(R.string.about_update_current)
                }
                is UpdateStatusEvaluator.Result.Available -> {
                    updateStatus.value =
                        context.getString(R.string.about_update_available, result.version)
                    applyAsset.value = release.assets.firstOrNull { it.url == product.url }
                    if (ProductUpdate.shouldPromptUpdate(appVersion, product.version, dismissed)) {
                        val url = product.url.ifBlank {
                            release.htmlUrl ?: ProductUpdate.RELEASES_PAGE
                        }
                        prompt = LaunchPrompt.Update(product.version, url)
                    }
                }
            }
        } catch (_: Exception) {
            // Stay silent — never block the app.
        }
    }

    when (val shown = prompt) {
        LaunchPrompt.Donate -> DonateNudgeDialog(
            onDonate = {
                scope.launch { appUpdatePreferences.markVersionSeen(appVersion) }
                prompt = null
                uriHandler.openUri(DonateLinks.VENMO_URL)
            },
            onNotNow = {
                scope.launch { appUpdatePreferences.markVersionSeen(appVersion) }
                prompt = null
            },
        )
        is LaunchPrompt.Update -> UpdateAvailableDialog(
            version = shown.version,
            onInstall = {
                scope.launch {
                    appUpdatePreferences.markChecked(System.currentTimeMillis(), shown.version)
                }
                prompt = null
                uriHandler.openUri(shown.url.ifBlank { ProductUpdate.RELEASES_PAGE })
            },
            onLater = {
                scope.launch {
                    appUpdatePreferences.markChecked(System.currentTimeMillis(), shown.version)
                }
                prompt = null
            },
        )
        null -> Unit
    }
}
