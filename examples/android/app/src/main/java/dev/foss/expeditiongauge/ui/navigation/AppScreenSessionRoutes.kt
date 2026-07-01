package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.export.EnhancedExportFormat
import dev.foss.expeditiongauge.export.HtmlSummaryExporter
import dev.foss.expeditiongauge.playback.PlaybackSessionLoader
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.stats.SessionComparison
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.playback.SessionListScreen
import dev.foss.expeditiongauge.ui.playback.SessionMetadataEditScreen
import dev.foss.expeditiongauge.ui.stats.SessionComparisonScreen
import dev.foss.expeditiongauge.ui.stats.SessionStatsDashboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppScreenSessionRoutes(
    screen: AppScreen,
    onScreenChange: (AppScreen) -> Unit,
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    statsSummaries: List<SessionStatsSummary>,
    statsAggregate: SessionAggregateStats,
    comparison: MutableState<SessionComparison?>,
    sessionStatsAggregator: SessionStatsAggregator,
    editingSessionId: Long?,
    onEditingSessionIdChange: (Long?) -> Unit,
    speedUnit: SpeedUnit = SpeedUnit.METRIC,
    pressureUnit: PressureUnit = PressureUnit.KPA,
) {
    when (screen) {
        AppScreen.Sessions -> SessionListScreen(
            database = services.database,
            statsSummaries = statsSummaries,
            onSessionSelected = { sessionId ->
                scope.launch {
                    PlaybackSessionLoader.load(services, sessionId)
                    onScreenChange(AppScreen.Playback)
                }
            },
            onSessionCompare = { leftId, rightId ->
                val left = statsSummaries.firstOrNull { it.sessionId == leftId }
                val right = statsSummaries.firstOrNull { it.sessionId == rightId }
                if (left != null && right != null) {
                    comparison.value = sessionStatsAggregator.compare(left, right)
                    onScreenChange(AppScreen.Comparison)
                }
            },
            onSessionEdit = { sessionId ->
                onEditingSessionIdChange(sessionId)
                onScreenChange(AppScreen.SessionEdit)
            },
            onSessionExportZip = { sessionId ->
                scope.launch {
                    val file = services.enhancedExportService.exportSession(
                        sessionId,
                        EnhancedExportFormat.ZIP,
                        context.cacheDir,
                    )
                    shareExportFile(context, file, "application/zip")
                }
            },
            onBack = { onScreenChange(AppScreen.Dashboard) },
        )
        AppScreen.SessionEdit -> editingSessionId?.let { sessionId ->
            SessionMetadataEditScreen(
                sessionId = sessionId,
                repository = services.sessionMetadataRepository,
                context = context,
                onSaved = { onScreenChange(AppScreen.Sessions) },
                onBack = { onScreenChange(AppScreen.Sessions) },
                onDeleteSession = if (FeatureFlags.mediaAttachmentsEnabled) {
                    {
                        scope.launch {
                            services.sessionDeleteService.deleteSession(sessionId)
                            onScreenChange(AppScreen.Sessions)
                        }
                    }
                } else {
                    null
                },
            )
        }
        AppScreen.Playback -> AppScreenPlaybackRoute(
            context = context,
            scope = scope,
            services = services,
            statsSummaries = statsSummaries,
            onBack = { onScreenChange(AppScreen.Sessions) },
            speedUnit = speedUnit,
            pressureUnit = pressureUnit,
        )
        AppScreen.Stats -> SessionStatsDashboard(
            sessions = statsSummaries,
            aggregate = statsAggregate,
            onPlay = { sessionId ->
                scope.launch {
                    PlaybackSessionLoader.load(services, sessionId)
                    onScreenChange(AppScreen.Playback)
                }
            },
            onCompare = { leftId, rightId ->
                val left = statsSummaries.first { it.sessionId == leftId }
                val right = statsSummaries.first { it.sessionId == rightId }
                comparison.value = sessionStatsAggregator.compare(left, right)
                onScreenChange(AppScreen.Comparison)
            },
            onExport = { summary ->
                scope.launch {
                    val events = services.database.sessionEventDao().getBySession(summary.sessionId)
                    shareHtmlSummary(context, HtmlSummaryExporter.export(summary, events))
                }
            },
            onBack = { onScreenChange(AppScreen.Dashboard) },
        )
        AppScreen.Comparison -> comparison.value?.let { cmp ->
            SessionComparisonScreen(
                comparison = cmp,
                onExport = {
                    shareHtmlSummary(context, HtmlSummaryExporter.exportComparison(cmp.left, cmp.right))
                },
                onGhostCompare = {
                    scope.launch {
                        PlaybackSessionLoader.loadWithGhost(services, cmp.left.sessionId, cmp.right.sessionId)
                        onScreenChange(AppScreen.Playback)
                    }
                },
                onBack = { onScreenChange(AppScreen.Stats) },
            )
        }
        else -> Unit
    }
}
