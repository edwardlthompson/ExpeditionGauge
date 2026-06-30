# Feature: Session Comparison

> Sprint 17 — side-by-side stats for two sessions.

## Acceptance criteria

- ✅ Compare peak latG, max β, best lap, slip event delta
- ✅ Per-card Compare from stats dashboard and session list
- ✅ HTML comparison export via share intent
- ✅ Optional ghost-lap map compare when `FeatureFlags.ghostLapEnabled`

## Container map

| Layer | Path |
|-------|------|
| Models | `stats/SessionComparison` in `SessionStatsAggregator.kt` |
| UI | `ui/stats/SessionComparisonScreen.kt` |
| Export | `export/HtmlSummaryExporter.exportComparison` |
