# Parallel agent scope map

Read @docs/PARALLEL_AGENT_SCOPES.md and BUILD_PLAN Parallel table.
Run `bash scripts/check-parallel-scope.sh`; abort dispatch on overlap.
Assign one branch per agent: `feature/agent-<task-slug>`. Shared schema stays Sequential-only.

## ExpeditionGauge forbidden paths (orchestrator + parallel agents)

| Path | Reason |
|------|--------|
| `BUILD_PLAN.md` | Sequential owner only |
| `COMPLETED_TASKS.md` | Sequential owner only |
| `examples/android/.../MainActivity.kt` | Composition root |
| `examples/android/.../ui/navigation/ExpeditionGaugeApp.kt` | App shell |
| `examples/android/.../ui/navigation/AppScreenRouter.kt` | Route wiring |
| `examples/android/.../ExpeditionGaugeServices.kt` | Service bootstrap |
| `examples/android/.../ui/layout/InsetAwareScaffold.kt` | Shared layout primitive (Sprint 19b lock) |
Canonical list: `FORBIDDEN_PATHS` in `scripts/lib/parallel_scope.py`.

## Sprint 19b — System UI insets (active)

> Dispatch after Sequential step **4** (`InsetAwareScaffold` wired). Merge before Sequential step **5** (inset tests). **agent_count_target: 5**

| Agent | Task | Isolated scope |
|-------|------|----------------|
| A — Record chrome | Record controls + mark-event FAB padding | `examples/android/.../ui/components/gauge/RecordControls.kt` (+ `MarkEventFab.kt`, same agent) |
| B — Scrubber | Playback scrubber bar padding | `examples/android/.../ui/playback/ScrubberMarkerStrip.kt` |
| C — Map | MapLibre `setPadding` on map attach | `examples/android/.../ui/playback/PlaybackMapView.kt` |
| D — G-meter sheet | Attitude `ModalBottomSheet` insets | `examples/android/.../ui/components/gauge/AttitudeGMeterGauge.kt` |
| E — Live sheet | Recording live strip sheet insets | `examples/android/.../ui/recording/RecordingLiveStrip.kt` |
**Dispatch:** `bash scripts/plan-parallel-dispatch.sh --require-sequential-clear --write-lock --json --feature 19b` then `/scope`.

## Sprint 20 — Dual-orientation HUD

> Dispatch after Sequential step **3**. **agent_count_target: 3**

| Agent | Task | Isolated scope |
|-------|------|----------------|
| A — Landscape HUD | `DashboardHudLandscape` | `examples/android/.../ui/dashboard/DashboardHudLandscape.kt` |
| B — Portrait HUD | `DashboardHudPortrait` | `examples/android/.../ui/dashboard/DashboardHudPortrait.kt` |
| C — Driving Mode | DataStore preference | `examples/android/.../settings/DrivingModePreferences.kt` |
## Sprint 21 — Android Auto

> Dispatch after Sequential step **4**. **agent_count_target: 3**

| Agent | Task | Isolated scope |
|-------|------|----------------|
| A — Car pane | `TelemetryPaneScreen` | `examples/android/car/.../car/ui/TelemetryPaneScreen.kt` |
| B — Settings | Android Auto toggle UI | `examples/android/.../ui/settings/SettingsAndroidAutoOptions.kt` |
| C — Feature flag | `FeatureFlags.androidAutoEnabled` | `examples/android/.../FeatureFlags.kt` |
## Rules

1. One branch per agent: `feature/agent-<task-slug>`
2. Run `bash scripts/check-parallel-scope.sh` before dispatch
3. Shared types/schemas: **Sequential agent only**
4. Never edit `BUILD_PLAN.md` from parallel agents
5. **Auto-dispatch:** `/scope` reads `plan-parallel-dispatch.sh` manifest and launches Task subagents; CLI fallback: `scripts/expedition/dispatch-parallel-agents.ps1`

## Collision Response

If `check-parallel-scope.sh` fails, split the task or move one item back to Sequential lane.
