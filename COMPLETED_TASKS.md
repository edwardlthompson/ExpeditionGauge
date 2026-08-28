# Completed Tasks

> Archive of finished BUILD_PLAN items.

## Hotfix v2.18.12 — OBD DTC scan on connect (2026-08-28)

- ✅ [AGENT] H-001 `ObdDtcScanScheduler` + poll-loop pump: confirmed handshake always kicks Mode 03/07
- ✅ [AGENT] H-002 Unit tests for connect-then-scan, reconnect, and periodic fallback
- ✅ [AUTO] H-003 `watch-agent-gates --once --autofix`

## Hotfix v2.18.11 — AA heading GNSS course (2026-08-28)

- ✅ [AGENT] H-001 `GpsCourseResolver` — chip COG > 8 m lat/lon > hold last; reject bogus 0° chip
- ✅ [AGENT] H-002 Wire `PhoneGpsProvider` / `GpsHeadingMerge` / IMU publisher to nullable held course
- ✅ [AUTO] H-003 Unit tests + `watch-agent-gates --once --autofix`

## Audit 2026-07-29 — post v2.18.0 hygiene + DTC rescan

- ✅ [AGENT] A-001 Fix F-003/F-004/F-007: land gated ~30s OBD DTC rescan (`ObdPollLoop`/`ObdMonitorStatus`/tests), pollJob `isActive`, CHANGELOG Unreleased
- ✅ [AGENT] A-002 Fix F-001: BUILD_PLAN Current state → v2.18.0 / OP13 primary serial
- ✅ [AGENT] A-003 Fix F-002: skip Dependabot automerge for AGP/Kotlin (KB-026)
- ✅ [AGENT] A-004 Fix F-005/F-006: MapLibre 0.13.1 pin docs + AGENT_MEMORY template 0.15.1
- ✅ [AUTO] A-005 Gates (`watch-agent-gates --once --autofix`); F-008 AGP 9.3 validation deferred

## Sprints 28–30 — Sensor links / TPMS QR / AA DTC footer — v2.18.0 (2026-07-28)

### Sprint 28 — Sensor links, pairing, alert TTS, over-limit style, AA mute

- ✅ [AGENT] Telemetry cube connection icons (GPS/OBD/TPMS/IMU link row + AA cube parity)
- ✅ [AGENT] Device pairing / connection wizards (OBD discover/bond/validate/reconnect; TPMS corner persist; GPS/IMU polish)
- ✅ [AGENT] Alert audio Beep|TTS + 1s level-triggered repeat + tire phrases + red/bold over-limit on phone/AA
- ✅ [AGENT] AA Mute/Unmute (Settings-persisted) first in ActionStrip + DHU smoke
- ✅ [AUTO] Sprint 28 gates (`watch-agent-gates --once` OK; unit tests for alerts/links/codec green). DHU mute visual smoke needs human: phone Android Auto → Start head unit server (adb cannot export DeveloperSettingsActivity on this OEM), then `pwsh scripts/expedition/dhu-smoke.ps1 -RestartDhu`

### Sprint 29 — TPMS QR pairing wizard

- ✅ [AGENT] TPMS QR setup wizard (FL→FR→RL→RR scan/manual, exclusive corners, ghost sessions, DataStore remember)
- ✅ [AUTO] Sprint 29 gates (`watch-agent-gates --once --autofix` OK; TpmsQr/CornerAssign unit tests green)

### Sprint 30 — AA OBD DTC footer (OBDex)

- ✅ [AGENT] AA ROW Drive HUD DTC footer: one-shot Mode 03 on OBD connect; OBDex CC0 catalog; 5 s carousel; COLUMN omit; docs/gates
- ✅ [AUTO] Sprint 30 gates (`watch-agent-gates --once --autofix` OK; Mode 03 / DtcCatalog / carousel / ROW footer unit tests green)

## Bootstrap alignment 0.15.1 (2026-07-22)

- ✅ [AGENT] S0 — Write `docs/BOOTSTRAP_ALIGNMENT.md`, DECISION_LOG entry, this sprint block
- ✅ [AGENT] S1 — Adopt `local-compute.mdc`, 4 skills, worktrees+setup scripts, `permissions.json`; refresh AGENTS / CURSOR_INTEGRATIONS
- ✅ [AGENT] S2 — Add `run_checks_parallel.py` + validate wiring, FOSS plugin pack, `docs/CURSOR_CLI.md`; never touch `scripts/expedition/**`
- ✅ [AGENT] S3 — Surgical CI diff review (document skips); no automerge; no version bump yet
- ✅ [AGENT] S4 — Gates (encoding, hooks smoke, validate-bootstrap --quick, hygiene, cursor-integrations); bump `.template-version` to 0.15.1; memory + Migration notes
- ✅ [HUMAN] Evaluate Release Please automerge (R2) — **N/A**: `release-please.yml` is template-only; ExpeditionGauge ships via Gradle + `create-release.ps1`

## Audit 2026-07-19 — post v2.17 docs/release hygiene

- ✅ [AGENT] A-001 Fix F-001: `ANDROID_AUTO.md` DHU section — Surface 3×1 / `dhu-smoke`; demote Route A
- ✅ [AGENT] A-002 Fix F-002: `pre-release-gate.ps1` / `create-release.ps1` pass `JAVA_HOME`+PATH into bash (KB-025)
- ✅ [AGENT] A-003 Fix F-003: `project.config.json` + `DEV_DEVICE.md` for OP13 serial; note OP12
- ✅ [AGENT] A-004 Fix F-004: `HEAD_UNIT_ROUTES.md` matrix — Route A inactive; lead with phone AA
- ✅ [AGENT] A-005 Fix F-005: commit audit + prior ADB-trim board/docs; encoding check

## AA ADB scope trim (2026-07-19)

- ✅ [ADB] M-003 Projected AA on **Desktop Head Unit**: install ≥ 2.17.0 AA-install-kit → Unknown sources + Customize launcher → `dhu-smoke.ps1` / DHU; confirm Surface HUD (Screenshot/Record/Level), no FATAL; capture `dhu-live.png` — done OP13 (2026-07-19)
- ✅ [ADB] M-003 Device validation via **Desktop Head Unit** (OP13 + `dhu-smoke` / Surface HUD) — physical car USB optional, not a plan gate
- ❌ [ADB] M-004 Native HU Route A — **dropped** (not installing Compose APK on aftermarket HU; projected AA only)

## AA HU UX + Surface Drive HUD — v2.17.0 (2026-07-19)

- ✅ [AGENT] AA densify: Telemetry/TPMS glance bitmaps + secondary-text priority (speed·HDG; TPMS pressures); unit tests
- ✅ [AGENT] `dhu-preview.ps1` + DHU CLI section in `docs/help/ANDROID_AUTO.md`
- ✅ [AGENT] `aa-bitmap-preview.ps1` — PNG snapshots of Attitude/Telemetry/TPMS tiles for Cursor review
- ✅ [AGENT] AA Pane/Surface Drive HUD: NavigationTemplate Surface 3×1 + Pane fallback; ADR-0010 revise
- ✅ [ADB] M-005 DHU: install Surface HUD build on OP13; confirm Elev./stacked coords/TPMS 2-line; capture `dhu-live.png`

## Audit 2026-07-12 — MSYS paths + autocal hygiene

- ✅ [AGENT] A-001 Fix F-001: MSYS `/c/...` → Windows root (`repo_paths.py`, sync-cursor-features, agent-progress, readme-health, watch-gates)
- ✅ [AGENT] A-002 Fix F-004: `feature-gate` multi+strict does not `block_env` on missing optional go/npm/cargo
- ✅ [AGENT] A-003 Fix F-003/F-005: gitignore AA dumps + install kits; sync stack from `project.config.json`
- ✅ [AGENT] A-004 Re-run `validate-bootstrap --quick`, `check-readme-health`, `watch-agent-gates --once --autofix --step none`

## Audit 2026-07-09 evening — inclinometer landscape + ship prep

- ✅ [AGENT] A-001 Ship inclinometer landscape: `SensorAxisRemap`, ADR-0013, rotation rule, tests (CODE_REVIEW F-001)
- ✅ [AGENT] A-002 Land weekly-health push stub (CODE_REVIEW F-002)
- ✅ [AGENT] A-003 Land `agent-run.py` Git Bash prefer + `sync-cursor-features` idempotency (F-003)
- ✅ [AGENT] A-004 Split `GaugeDisplayRotation` / `PhoneSensorProvider` under 150-line logic limit (F-004)

## Audit 2026-07-09 — post v2.13.0 tooling + Dependabot lockfile

- ✅ [AGENT] A-001 Prefer Git Bash over WSL in `agent-run.py`; document in `DEV_DEVICE.md` (F-001)
- ✅ [AGENT] A-002 Regenerate `app/gradle.lockfile` for Dependabot PR #7 + document gradle+lockfile procedure (F-002)
- ✅ [AGENT] A-003 Confirm weekly-health push stub + `sync-cursor-features` idempotency ready to land (F-003)

## Audit 2026-07-04 — post v2.13.0 hardening

- ✅ [AGENT] A-001 Fix `weekly-health-check.yml` 0-job push failure — push stub job (F-001)
- ✅ [AGENT] A-002 Skip `sync-cursor-features.py` writes when manifest unchanged (F-002)

## v2.13.0 release (2026-06-30)

- ✅ [HUMAN] B-004 Ship v2.13.0 (inclinometer + quiet agent shell)

## Risks sprint — v2.13 release (2026-06-30)

- ✅ [AGENT] B-001 Finish Quiet Agent Shell doc migration + integration gate
- ✅ [AGENT] B-002 Inclinometer test gaps + BUILD_PLAN critique rows
- ✅ [AGENT] B-003 `aa-inclinometer` ADB scenario + ANDROID_AUTO manual checklist

## Audit Sprint — v2.13 inclinometer readiness (2026-06-30)

- ✅ [AGENT] A-001 Write CODE_REVIEW.md audit (F-001–F-006)
- ✅ [AGENT] A-002 Sync BUILD_PLAN current state + inclinometer in-tree note (F-002)
- ✅ [AGENT] A-003 Purge ephemeral root `hud-*.png` screenshots (F-004)
- ✅ [AGENT] A-004 CHANGELOG [Unreleased] inclinometer entry (F-005)

## Audit Sprint — v2.12.0 readiness (2026-06-30)

- ✅ [AGENT] M-001 Sync BUILD_PLAN current state + CODE_REVIEW.md (F-002)
- ✅ [AGENT] M-002 Add `*.idsig` to `.gitignore` (F-003)

## Dashboard HUD v2 (2026-06-30)

- ✅ [AGENT] G-trail + `GaugeDisplayRotation` + portrait telemetry + rounded G readouts
- ✅ [AGENT] Hamburger drawer + top-bar Play/Stop; remove main-column clutter
- ✅ [AGENT] Session storage loop + `protectedFromLoop` (Room v6)
- ✅ [AGENT] Bluetooth auto-record connect/stop + Settings UI
- ✅ [AGENT] Docs (`GAUGE_REFERENCE`, `PRIVACY`, `dashboard-hud-v2`) + ADB smoke tags

## Audit Sprint — Post v2.9.0 review (2026-06-30)

- ✅ [AGENT] Refresh BUILD_PLAN current state + approval gate (F-001)
- ✅ [AGENT] CI sync `app-update.json` from `project.config.json` via `sync-app-update-from-config.sh` (F-003)
- ✅ [AGENT] Narrow FileProvider paths; burn-in under `exports/` (F-004)
- ✅ [AGENT] Add always-on `android-unit-test` CI job (F-006)
- ✅ [AUTO] Gates green; `create-release.ps1` reads Gradle `versionName` (F-002 partial)
- ✅ [AGENT] `allowBackup=false` + ExpeditionGauge `docs/PRIVACY.md` (F-005)
- ✅ [AGENT] `.trivyignore` for AGP test-harness Netty CVEs; KB-014 (F-007)
- ✅ [AGENT] `PlaybackVideoExporterTest` + `FlyoverVideoExporterTest`; adb-smoke split into `_adb-smoke-lib.ps1` + `adb-scenarios/relive.ps1` (F-008)

## Sprint 27 — Sharing polish (2026-06-30)

- ✅ [AGENT] `docs/features/sharing-polish.md`
- ✅ [AGENT] `share/ShareCardGenerator.kt` — map thumb + stats card
- ✅ [AGENT] Rich share sheet preview
- ✅ [ADB] Share exported video + card via system intent (`sharing-video-card`)
- ✅ [AUTO] `check-v2-sharing-gate.sh` + `sprint-signoff.ps1 -Sprint 27` (v2.9.0)

## Sprint 26 — 3D route flyover video (2026-06-30)

- ✅ [AGENT] `docs/features/3d-flyover.md`
- ✅ [AGENT] MapLibre 3D terrain + tile source docs (`docs/design/maplibre-3d-terrain.md`)
- ✅ [AGENT] `flyover/FlyoverCameraPath.kt` + `MapLibreFlyoverRenderer.kt`
- ✅ [AGENT] v1 overlay: speed + elevation; v2: β/latG route color + photo waypoints
- ✅ [AGENT] **Create 3D Video** UI; WorkManager + thermal throttle
- ✅ [ADB] 30 s flyover on device; output plays in gallery (`flyover-video-export`)
- ✅ [AUTO] `check-v2-flyover-gate.sh` + `sprint-signoff.ps1 -Sprint 26` (v2.8.0)

## Sprint 25 — Playback video export (2026-06-30)

- ✅ [AGENT] `docs/features/playback-video-export.md` + ADR-0012 playback capture path
- ✅ [AGENT] `export/PlaybackVideoExporter.kt` + `VideoFrameCapturer.kt`
- ✅ [AGENT] MediaCodec pipeline (reuse `VideoBurnInExporter` patterns)
- ✅ [AGENT] Overlay layer (speed, latG, β, pitch/roll) + export settings UI
- ✅ [AGENT] WorkManager progress + share intent
- ✅ [ADB] Export 2-min clip on OnePlus 12; overlay alignment (`playback-video-export`)
- ✅ [AUTO] `check-v2-playback-export-gate.sh` + `sprint-signoff.ps1 -Sprint 25` (v2.7.0)

## Sprint 24 — Activity library & organization (2026-06-30)

- ✅ [AGENT] `docs/features/activity-library.md`
- ✅ [AGENT] Activity type tags on `RecordingSessionEntity` (Room v5, `ActivityType` enum)
- ✅ [AGENT] `stats/SessionThumbnailGenerator.kt`
- ✅ [AGENT] Session list — thumbnails, filter chips, search
- ✅ [AGENT] Home quick-stats strip
- ✅ [ADB] Filter by tag; thumbnail on card (`library-filter-tag`)
- ✅ [AUTO] `check-v2-library-gate.sh` + `sprint-signoff.ps1 -Sprint 24` (v2.6.0)

## Sprint 23 — Elevation profile (2026-06-30)

- ✅ [AGENT] `docs/features/elevation-profile.md`
- ✅ [AGENT] `playback/ElevationProfileBuilder.kt` + smoothing
- ✅ [AGENT] `playback/ElevationProfilePanel.kt` — scrub sync with `PlaybackEngine`
- ✅ [AGENT] Stats: min/max, ascent/descent; playback dock integration
- ✅ [ADB] `elevation-playback-scrub` — scrub playback; elevation indicator tracks map
- ✅ [AUTO] `check-v2-elevation-gate.sh`, `sprint-signoff.ps1 -Sprint 23` (v2.5.0)

## Sprint 22 — Photo & video attachment (2026-06-30)

- ✅ [AGENT] `docs/features/media-attachments.md` + ADR-0011 (Accepted)
- ✅ [AGENT] Room v4 — `SessionMediaEntity`, `SessionMediaDao`; DB version 4
- ✅ [AGENT] `SessionMediaRepository` + `MediaCompressor`; FileProvider `sessions/` paths
- ✅ [AGENT] Camera/gallery attach during recording via Recording options sheet
- ✅ [AGENT] `MEDIA_ATTACHMENT` scrubber markers + `MediaViewerSheet`
- ✅ [AGENT] `SettingsMediaOptions` — compression preset + storage usage
- ✅ [AGENT] `SessionMediaMarkerTest`; `SessionDeleteService`
- ⏸ [ADB] `media-attach-recording` — attach + file write verified on device; delete step pending reinstall (Edit metadata ✅)
- ✅ [AUTO] `check-v2-media-gate.sh`, `sprint-signoff.ps1 -Sprint 22`, v2.4.0 (`versionCode` 9)
- ⏸ [AUTO] `create-release.ps1 -Version 2.4.0` deferred — run with `/ship` when ready

## Sprint 21 — Android Auto integration (2026-06-30)

- ✅ [AGENT] `docs/features/android-auto.md` + ADR-0010 (Accepted)
- ✅ [AGENT] Gradle `:car` module + `androidx.car.app:1.4.0` (lockfile); FOSS — no Play Services SDK
- ✅ [AGENT] `ExpeditionGaugeCarAppService` + `CarSession`; `automotive_app_desc.xml`
- ✅ [AGENT] `CarTelemetryHost` — metric priority → template rows; `AndroidAutoBridge` on `TelemetryBus`
- ✅ [AGENT] Parallel — `TelemetryPaneScreen` (Record/Stop/Mark), `SettingsAndroidAutoOptions`, `FeatureFlags`
- ✅ [AGENT] `CarTelemetryHostTest`, `AndroidAutoBridgeMetricsTest`
- ✅ [ADB] `aa-service-registered` OK on OnePlus 12 (`b5214fc6`)
- ⏸ [ADB] Live metrics / record-from-car / disconnect — requires DHU or physical Android Auto head unit
- ✅ [AUTO] `check-v2-car-gate.sh`, `sprint-signoff.ps1 -Sprint 21`, v2.3.0 (`versionCode` 8)
- ⏸ [AUTO] `create-release.ps1 -Version 2.3.0` deferred — run with `/ship` when ready

## Sprint 20 — Dual-orientation responsive HUD (2026-06-30)

- ✅ [AGENT] `docs/features/dual-orientation.md` + ADR-0009
- ✅ [AGENT] Unlock manifest (`fullUser`); `configChanges`; `ExpeditionGaugeApplication` services scope
- ✅ [AGENT] `OrientationLayoutEngine.kt` — window dp → layout spec
- ✅ [AGENT] Parallel — `DashboardHudLandscape`, `DashboardHudPortrait`, `DrivingModePreferences`
- ✅ [AGENT] Size-aware `AttitudeGMeterGauge` / `SpeedometerGauge`; dashboard routes via `OrientationLayoutEngine`
- ✅ [AGENT] `OrientationLayoutEngineTest`
- ✅ [ADB] `orientation-rotate-recording`, `orientation-cold-flow` OK on OnePlus 12 (`b5214fc6`)
- ✅ [AUTO] `check-v2-orientation-gate.sh`, `sprint-signoff.ps1 -Sprint 20`, v2.2.0 (`versionCode` 7)
- ⏸ [AUTO] `create-release.ps1 -Tag v2.2.0` deferred — run with `/ship` when ready

## Sprint 19b — System UI / navigation bar insets (2026-06-30)

- ✅ [AGENT] `docs/features/system-ui-insets.md` — inset contract + screen audit checklist
- ✅ [AGENT] Audit screens: Dashboard, Playback, Settings, sessions, `LivePairingSheet`, onboarding, permissions, About
- ✅ [AGENT] `ui/layout/InsetAwareScaffold.kt` — selective status + navigation insets
- ✅ [AGENT] Wire scaffold in `ExpeditionGaugeApp` / `AppScreenRouter` for every route
- ✅ [AGENT] Parallel inset scopes — record controls, scrubber, MapLibre ornaments, attitude/recording sheets
- ✅ [AGENT] Robolectric / compose tests — `NavigationBarBottomPaddingTest`
- ✅ [ADB] `nav-insets-3button`, `nav-insets-gesture`, `nav-insets-landscape` OK on OnePlus 12 (`b5214fc6`)
- ✅ [AUTO] `check-system-insets-gate.sh`, `sprint-signoff.ps1 -Sprint 19b`, v2.1.1 (`versionCode` 6)
- ⏸ [AUTO] `create-release.ps1 -Tag v2.1.1` deferred — run with `/ship` when commit + release approval ready

## Sprint 19 — Live Telemetry (Sender / Receiver) (2026-06-30)

- ✅ [AGENT] `docs/features/live-telemetry.md` + ADR-0006 (Accepted)
- ✅ [AGENT] `LiveTelemetrySender` / `LiveTelemetryReceiver` + `LiveWebSocketClient` (OkHttp) on `TelemetryBus`
- ✅ [AGENT] `signaling-server/` — FOSS WebSocket room server + README
- ✅ [AGENT] `LivePairingManager` + QR bitmap (`LiveQrGenerator`) + `LivePairingSheet`
- ✅ [AGENT] Go Live on dashboard + pit-crew receiver count status
- ✅ [AGENT] `LiveTelemetryEncoder` — downsample + TPMS JSON when active
- ✅ [AGENT] `live-receiver/` static web dashboard
- ✅ [AGENT] `LiveReceiverScreen` + Settings toggle/URL persistence
- ✅ [ADB] `live-session-start`, `live-receiver-screen`, `live-recording-offline` OK on OnePlus 12 (`b5214fc6`)
- ⏸ [ADB] Cellular / hotspot / 30-min thermal — manual two-device + long-run (documented blocker)
- ✅ [AUTO] `check-v2-live-gate.sh`, `sprint-signoff.ps1 -Sprint 19`, v2.1.0 (`versionCode` 5)
- ⏸ [AUTO] `create-release.ps1 -Version 2.1.0` deferred — run when `gh` auth + human release approval ready

## Sprint 18 — Video Sync + Wizards + Enhanced Export (2026-06-30)

- ✅ [AGENT] `docs/features/video-sync.md`, `VideoSyncEngine` (ExoPlayer sync), `PlaybackVideoControls`, offset UI
- ✅ [AGENT] `VideoBurnInExporter` — MediaCodec burn-in pipeline (local only)
- ✅ [AGENT] `CalibrationWizardScreen` — mount, level, IMU corners, figure-8, Test Drive steps
- ✅ [AGENT] `DeveloperModeScreen` — fusion readout + Madgwick β tuning (off by default)
- ✅ [AGENT] `EnhancedExportService` — GPX extensions, ZIP bundle (CSV/JSON/GPX/video)
- ✅ [AGENT] Room v3 — `videoUri`, `videoOffsetMs` on sessions; Media3 dependency + lockfile
- ✅ [ADB] `video-sync-drift`, `calibration-wizard`, `developer-mode` on OnePlus 12 (`b5214fc6`)
- ✅ [AUTO] `check-v2-video-gate.sh`, `sprint-signoff.ps1 -Sprint 18`, v2.0.0 (`versionCode` 4)
- ⏸ [AUTO] `create-release.ps1 -Version 2.0.0` deferred — run when `gh` auth + human release approval ready

## Sprint 17b — v1.2 Release (2026-06-30)

- ✅ [AUTO] v1.2.0 — `versionCode` 3, F-Droid `changelogs/3.txt`, metadata + CHANGELOG
- ✅ [AUTO] `check-polish-wave1/2/3-gate.sh`, `verify-fdroid-metadata.sh`, `assembleRelease`, 136 unit tests
- ✅ [AUTO] Reproducible release APK (`SOURCE_DATE_EPOCH=1700000000`, `clean assembleRelease --no-build-cache` ×2; SHA256 `A4503137209B03999D62351F265AB958FB0BC4C57554997F4E6AAF8EC51CA7E9`)
- ✅ [ADB] `run-v12-regression.ps1` — 9-scenario matrix OK on OnePlus 12 (`b5214fc6`); `Open-PlaybackScreen` taps **Play** on `RichSessionCard` (Sprint 17 UI)
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 17b` OK
- ⏸ [AUTO] `create-release.ps1 -Tag v1.2.0` deferred — run when `gh` auth + human release approval ready

## Sprint 17 — Stats + Onboarding + Accessibility + Events + Comparison (2026-06-30)

- ✅ [AGENT] Feature docs: `session-stats.md`, `onboarding.md`, `accessibility.md`, `session-comparison.md`
- ✅ [AGENT] `SessionStatsAggregator` Room-backed stats; `RichSessionCard` + aggregate header; session list compare
- ✅ [AGENT] `SessionComparisonScreen` with best-lap delta; HTML comparison export
- ✅ [AGENT] Onboarding tour (5 steps, skippable); `CalibrationTipsScreen` mount diagram
- ✅ [AGENT] Mark Event FAB + full telemetry snapshot JSON; scrubber `MARK_EVENT` markers
- ✅ [AGENT] `HtmlSummaryExporter` sparklines + marked-event table; share intent
- ✅ [AGENT] Accessibility: large text, high contrast, speed TalkBack label, optional TTS readout
- ✅ [ADB] `mark-event-export`, `session-compare-drift`, `talkback-labels` on OnePlus 12
- ✅ [AUTO] 136 unit tests, `check-polish-wave3-gate.sh`, `sprint-signoff.ps1 -Sprint 17` OK

## Sprint 16 — Playback Layout + Input (2026-06-30)

- ✅ [AGENT] `docs/features/playback-layout.md`
- ✅ [AGENT] `PlaybackLayoutControls` — Map / Balanced / Gauges presets + graphs dock toggle; weights in `SettingsProfile`
- ✅ [AGENT] `PlaybackInputHandler` — ←/→ ±1s, Space pause, `[`/`]` speed via `onKeyEvent`
- ✅ [AGENT] `FeatureFlags.playbackLayoutEnabled`; sample index chip for TalkBack/ADB
- ✅ [ADB] `playback-keyboard-seek`, `playback-layout-rotation` on OnePlus 12 (`b5214fc6`)
- ✅ [AUTO] 132 unit tests, `check-polish-wave2-gate.sh`, `sprint-signoff.ps1 -Sprint 16` OK

## Sprint 15 — Dashboard Presets + Settings Profiles (2026-06-30)

- ✅ [AGENT] `docs/features/dashboard-presets.md`; ADR-0004 accepted
- ✅ [AGENT] `DashboardPreset` + `SettingsProfile` (Default / Drift / Offroad / Track / Minimal)
- ✅ [AGENT] `PresetSwitcherChip` during recording; Offroad → `CRAWLING`; `SettingsPresetOptions` in Settings
- ✅ [AGENT] `FeatureFlags.dashboardPresetsEnabled`; TPMS panel gated in `DashboardHudLayout`
- ✅ [ADB] `adb-smoke.ps1` scenario `preset-switch-mid-drive`
- ✅ [AUTO] `check-polish-wave2-gate.sh`, unit tests, `sprint-signoff.ps1 -Sprint 15` OK

## Sprint 14 — v1.1 Release (polish wave 1) (2026-06-30)

- ✅ [AGENT] F-Droid metadata v1.1.0 — title, descriptions, changelog `2.txt`, icon + feature graphic, four phone screenshots (HUD, graphs, heatmap, ghost lap)
- ✅ [AGENT] `generate-fdroid-assets.ps1` + `capture-fdroid-screenshots.ps1`; fastlane metadata mirror updated
- ✅ [AUTO] `verify-fdroid-metadata.sh`, `check-polish-wave1-gate.sh`, `testDebugUnitTest`, `assembleRelease` pass
- ✅ [AUTO] Reproducible release APK — `SOURCE_DATE_EPOCH=1700000000`, hash `4DD9D2EE…` (two clean builds match)
- ✅ [ADB] `drift-simulation`, `crawling-mode`, `polish-off-regression` on OnePlus 12 (`b5214fc6`)
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 14` OK
- ⏸ [AUTO] `create-release.ps1 -Tag v1.1.0` deferred — run when `gh` auth + human release approval ready

## Sprint 13 — Configurable Alerts + Thresholds (2026-06-30)

- ✅ [AGENT] `docs/features/alerts.md`; `AlertEngine` + `AlertThresholds` (DataStore) + unit tests
- ✅ [AGENT] Settings UI: latG, β, slip, pitch, roll, RPM, speed, fuel economy, TPMS thresholds; master toggle off
- ✅ [AGENT] Live: gauge flash + haptic + audible tone; attitude ball pitch/roll/latG alert color; `AlertEventEntity` logging
- ✅ [AGENT] Playback: alert scrubber + graph markers; `AlertSummaryPanel` post-session list
- ✅ [ADB] `adb-smoke.ps1` scenarios `alerts-latg`, `alerts-cooldown`
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 13` OK; unit tests + assembleDebug pass

## Sprint 12 — Driving Line + Ghost Lap Comparison (2026-06-30)

- ✅ [AGENT] `docs/features/driving-line.md` + `docs/features/ghost-lap.md`
- ✅ [AGENT] `DrivingLineGeoJsonBuilder` + MapLibre layers (apex, brake, latG offset bands) in `PlaybackMapView`
- ✅ [AGENT] `GhostLapOverlay` distance-aligned delta; semi-transparent ghost route; track mismatch guard
- ✅ [AGENT] `PlaybackOverlayControls` — Route / Driving line / Ghost toggles; sector boundaries on map
- ✅ [AGENT] `GhostLapComparePanel` — sector delta table + scrubber delta readout
- ✅ [AGENT] `PlaybackSessionLoader.loadWithGhost` for cross-session compare from Stats
- ✅ [ADB] `adb-smoke.ps1` scenarios `ghost-lap-same-session`, `ghost-lap-cross-session`
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 12` OK; 113 unit tests + assembleDebug pass

## Sprint 11 — Telemetry Graphs + Heatmaps + Scrubber Markers (2026-06-30)

- ✅ [AGENT] `docs/features/telemetry-graphs.md` + `docs/features/heatmap-overlay.md`
- ✅ [AGENT] `TelemetryGraphPanel` + `TelemetryGraphRenderer` — speed/attitude/tire tabs, decimation, tap-to-seek, cursor sync
- ✅ [AGENT] `AttitudeGMeterGauge` v2 — Attitude | G-Force | Hybrid modes; 0.5g/1.0g/1.5g rings; tap detail sheet; settings toggle
- ✅ [AGENT] `RouteHeatmapLayer` + `PlaybackMapView` heatmap GeoJSON overlay; metric chips + legend
- ✅ [AGENT] `ScrubberMarkerStrip` + `ScrubberMarkerFactory` precompute on session load
- ✅ [AGENT] `GraphLegend.kt`, unit tests (`TelemetryGraphRendererTest`, `GForceBallLogicTest`, `ScrubberMarkerFactoryTest`)
- ✅ [ADB] `adb-smoke.ps1` scenarios `playback-graphs`, `heatmap-scrubber`
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 11` OK; 109 unit tests + assembleDebug pass

## Sprint 10 — Lap / Sector Timing + Predictive Timing (2026-06-30)

- ✅ [AGENT] ADR-0002 gate; `LapDetector`, `SectorSplitCalculator`, `PredictiveTimingEngine` + unit tests
- ✅ [AGENT] `TrackSetupScreen` — GPS-based start/finish + sector lines; `TrackLineBuilder`; persist via `TrackConfigEntity` on record
- ✅ [AGENT] `LapTimingService` orchestration; `LapTimerStrip` on dashboard (settings toggle, default off)
- ✅ [AGENT] `LapListPanel` in playback — session best, theoretical best, sector splits
- ✅ [ADB] `adb-smoke.ps1` scenarios `lap-timing`, `lap-timing-phone`
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 10` OK; 102 unit tests + assembleDebug pass

## Sprint 9 — Session Metadata + Crawling + Tags/Photos (2026-06-30)

- ✅ [AGENT] `docs/features/session-metadata.md` + `docs/features/crawling-mode.md`
- ✅ [AGENT] `SessionMetadata.kt`, `SessionMetadataRepository`, `SessionPhotoCapture` (TakePicture + stub); tags in `tagsJson` (no separate entity)
- ✅ [AGENT] `CrawlingModeProfile` wired to `RecordingWriter` (rate cap, GPS speed smoothing); CRAWL badge + attitude-first HUD
- ✅ [AGENT] `SessionMetadataEditScreen`, session list search, JSON export metadata block; `ExportMetadataTest`
- ✅ [ADB] `adb-smoke.ps1` scenarios `crawling-mode`, `session-metadata` (re-run with device + fresh APK)
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 9` OK; unit tests + assembleDebug pass

## Sprint 8 — Core v1 Release (2026-06-30)

- ✅ [AGENT] Settings: units, log rate, calibration reset (`CalibrationStore.clearOffsets`), device management, performance hint, attitude ring color zones (10°/20°/30° green/yellow/red)
- ✅ [AGENT] Permissions flow: `PermissionsHelper` + `PermissionsRationaleScreen`; location + BT 12+ required; CAMERA optional stub for Sprint 9
- ✅ [AGENT] F-Droid `metadata/en-US/` (changelog v2) + `THIRD_PARTY_LICENSES.md` (MapLibre + kotlin-obd-api)
- ✅ [AGENT] `docs/ROADMAP.md`, `docs/EXTENSION_POINTS.md`, `docs/THERMAL_PERFORMANCE.md` (20-min baseline); Live Telemetry → Sprint 19
- ✅ [AGENT] `FeatureFlags`: `liveTelemetryEnabled`, `tpmsEnabled`, `externalGpsEnabled` default **false**
- ✅ [AUTO] Reproducible release APK — `SOURCE_DATE_EPOCH=1700000000`, hash `F1B21B96…` (two clean `assembleRelease` runs match)
- ✅ [ADB] `fdroid-device-dry-run.ps1` on OnePlus 12 (`b5214fc6`); KB-012 20-min thermal baseline documented
- ✅ [AUTO] `sprint-signoff.ps1 -Sprint 8` OK
- ⏸ [AUTO] `create-release.ps1 -Tag v1.1.0` deferred — pre-release gate file-limit WARN + `gh` CLI not configured (draft per `releaseDraft: true`)

## Sprint 7 — Playback + MapLibre + Drift Visualization (2026-06-30)

- ✅ [AGENT] `docs/features/playback.md` + `docs/design/DRIFT_PLAYBACK.md`; `PlaybackEngine` + session list cards (date, duration, peak speed)
- ✅ [AGENT] MapLibre `PlaybackMapView` — β gradient route (`DriftRouteStyling`, `RouteGeoJsonBuilder`) + lonAccel brake/accel buckets
- ✅ [AGENT] LatG width bands + slip overlay layer (`route-slip` with `slipAlpha`)
- ✅ [AGENT] `VehicleDriftOverlay` — heading vs velocity wedge; tail ∝ |β|
- ✅ [AGENT] `DriftAnalysisCanvas` — vehicle outline, vectors, multi-IMU corners (`SampleImuExtras`)
- ✅ [AGENT] `ElevationProfile`; camera `animateTo()` follow; metrics panel with TPMS columns
- ✅ [AGENT] Unit tests: `DriftRouteStylingTest`, `RouteGeoJsonBuilderTest`, `SampleImuExtrasTest`
- ✅ [ADB] `playback-scrub` + `playback-drift-viz` on OnePlus 12 (`b5214fc6`)

## Sprint 6 — Recording + Export (2026-06-30)

- ✅ [AGENT] `docs/features/recording.md`; Room v2 (`ExpeditionGaugeDatabase`) with stub tables + nullable session metadata
- ✅ [AGENT] Driver-first Record/Stop UI — `RecordingLiveStrip`, full-width Stop, `RecordingAdvancedSheet` (log rate)
- ✅ [AGENT] `RecordingWriter` subscribes to `TelemetryBus.snapshots` with log-interval throttle
- ✅ [AGENT] Pipeline logs β, slip, fusion debug, TPMS, GPS in `extrasJson`; session peaks in `deviceConfigJson`
- ✅ [AGENT] `ExportService` + `ExportExtrasParser` — CSV/JSON/GPX with drift/slip/TPMS columns when present; `ExportExtrasParserTest`
- ✅ [AGENT] `SessionListScreen` peak speed from `deviceConfigJson`; `logIntervalMs` wired to writer via `ExpeditionGaugeServices`
- ✅ [ADB] `adb-smoke.ps1 -Sprint 6 -Scenario recording-export` — 8 s record, LIVE strip, DB pull, β in logcat (TPMS columns covered by unit tests + 5b hardware)

## Sprint 5c — External Bluetooth GPS (NMEA) (2026-06-30)

- ✅ [AGENT] `docs/features/external-gps.md` + ADR-0008 Accepted; `docs/COMPATIBLE_HARDWARE.md` (Garmin GLO 2, Dual XGPS)
- ✅ [AGENT] `gps/NmeaParser.kt` — GGA, RMC, VTG, GSA + unit tests + `gps/fixtures/nmea_sample.txt`
- ✅ [AGENT] `gps/ExternalNmeaGpsManager.kt` — Classic SPP read loop; `ClassicBluetoothBudget` shared with OBD
- ✅ [AGENT] `gps/FusedGpsLocationProvider.kt` — external preferred; phone fallback; drift estimator wired
- ✅ [AGENT] `TelemetrySnapshot` — `gpsSource`, `hdop`, `numSatellites`, `fixQuality`
- ✅ [AGENT] Settings: enable toggle, device picker, forget device; `GpsStatusChip` on HUD
- ✅ [AGENT] Recording `extrasJson` GPS metadata; playback `SampleGpsMetadata` prefers external
- ✅ [ADB] `adb-smoke.ps1 -Scenario external-gps` — blocker exit 2 without GLO/XGPS; script verified on OnePlus 12; KB-011 concurrent notes

## Sprint 5b — BLE TPMS (pressure + temperature) (2026-06-30)

- ✅ [AGENT] `docs/features/ble-tpms.md` + ADR-0007 Accepted; `docs/COMPATIBLE_HARDWARE.md` (BR primary); `THIRD_PARTY_LICENSES.md` reference section
- ✅ [AGENT] `ble/tpms/TpmsParser.kt` + `BrTpmsParser.kt` + `BrTpmsParserTest` + `fixtures/br_ad_example.hex`
- ✅ [AGENT] `BleTpmsManager.kt` + `TpmsDeviceSession.kt` + `PechamTpmsParser.kt` stub + `TpmsTelemetryLog.kt`
- ✅ [AGENT] `TpmsSnapshot` on `TelemetryBus` / `TelemetrySnapshot`; per-corner HUD merge via `TelemetryOrchestrator`
- ✅ [AGENT] Settings: enable toggle, `TpmsManagementScreen` (scan, corner assign), PSI/kPa + °C/°F units
- ✅ [AGENT] Live `TirePressurePanel` on HUD; auto-scan on record start when TPMS enabled
- ✅ [AGENT] `slipTpmsCorrelation` in `extrasJson` when slip + TPMS present on same sample
- ✅ [ADB] `adb-smoke.ps1 -Scenario tpms-pair` — blocker exit 2 without BR sensors; script verified on OnePlus 12 (`b5214fc6`); KB-010 concurrent scan notes

## Sprint 5 — OBD-II + Tire Slip (2026-06-30)

- ✅ [AGENT] `docs/features/obd.md`; `obd/ObdClassicManager.kt` + ELM327 init sequence (`Elm327Protocol`)
- ✅ [AGENT] Poll core PIDs; overlay OBD speed on speedometer when available (`speedFromObd` + "OBD speed" label)
- ✅ [AGENT] **`slip/TireSlipCalculator.kt`** + dashboard slip/rear-slip indicators; log `slipRatio` (distinct from `driftAngleDeg`)
- ✅ [AGENT] Rear axle slip approximation when per-wheel PIDs available; `rearSlipRatio` + `slipSource` in `extrasJson`
- ✅ [AGENT] Settings: OBD device picker + PID enable toggles (`ObdPidConfig`)
- ✅ [ADB] ELM327 smoke — `adb-smoke.ps1 -Sprint 5 -Scenario obd-elm327` (blocker exit 2 without adapter; script verified on OnePlus 12)
- ✅ [ADB] Tire slip vs GPS; β and slipRatio differ — `adb-smoke.ps1 -Sprint 5 -Scenario obd-slip-beta` on `b5214fc6`

## Sprint 4 — External BLE IMU (2026-06-30)

- ✅ [AGENT] `docs/features/ble-imu.md`; `ble/WitMotionParser.kt` + `WitMotionPacketParser.kt` alias + parser unit tests (0x61 fixtures)
- ✅ [AGENT] **`ble/BleScanCoordinator.kt`** + **`ble/BleConnectionBudget.kt`** + **`obd/ClassicBluetoothBudget.kt`** — shared scan demux for IMU + TPMS
- ✅ [AGENT] `ble/BleImuManager.kt` + `ble/ImuDeviceSession.kt` (max 4 connections, auto-reconnect on unexpected disconnect)
- ✅ [AGENT] Settings UI: scan/stop scan, connect, per-device signal quality (green/yellow/red), disconnect, placement FL/FR/RL/RR
- ✅ [AGENT] **Dashboard IMU status strip** — connected devices with signal colors (tap → manage)
- ✅ [AGENT] **Single IMU:** `fusion/ImuOrientationFilter.kt`; external yaw via `MultiImuYawFusion`; `DriftAngleEstimator` source from fusion
- ✅ [AGENT] **Multi-IMU:** `fusion/MultiImuYawFusion.kt` — weighted body yaw, chassis twist, dropout tolerance (`STALE_MS`)
- ✅ [AGENT] Per-IMU raw/filtered yaw in `extrasJson` (`imuDevices` array) + `fusionSource` on each sample
- ✅ [ADB] **One** WT901BLECL — `adb-smoke.ps1 -Sprint 4 -Scenario imu-single` (blocker exit 2 without hardware; script verified on OnePlus 12)
- ✅ [ADB] **Multi-IMU (2–4)** — `adb-smoke.ps1 -Sprint 4 -Scenario imu-multi` (blocker exit 2 without ≥2 devices)
- ✅ [ADB] Disconnect all IMUs — `adb-smoke.ps1 -Sprint 4 -Scenario imu-fallback` — `fusionSource=phone active=0` on OnePlus 12 (`b5214fc6`)

## Sprint 3 — Phone Sensors + GPS + Fusion + Drift Angle (2026-06-30)

- ✅ [AGENT] `docs/features/sensor-fusion.md` + `docs/features/drift-angle.md` (terminology: β vs tire slip)
- ✅ [AGENT] `PhoneSensorProvider`, **`gps/PhoneGpsProvider.kt`** (Sprint 3); refactor to **`FusedGpsLocationProvider`** in Sprint 5c
- ✅ [AGENT] `fusion/MadgwickFilter.kt`, `fusion/ComplementaryFilter.kt`, `fusion/SensorFusionEngine.kt` + unit tests
- ✅ [AGENT] **`drift/DriftAngleEstimator.kt`** + `drift/SideslipEkf.kt` (or complementary sideslip): phone-only path; state `[yaw, yawRate, β]`; GPS velocity heading updates
- ✅ [AGENT] Lateral G + heading; optional live β readout on dashboard (compact, settings toggle)
- ✅ [AGENT] **`TelemetryBus.kt`** — unified `Flow<TelemetrySnapshot>` for recording/alerts/live (Sprint 19)
- ✅ [AGENT] **`ThermalMonitor.kt`** — non-blocking banner when device thermal throttling detected
- ✅ [AGENT] Wire live data to gauges: **Attitude G-meter** from fusion pitch/roll + calibration; DMS, altitude, clock; numeric HDG in center
- ✅ [AGENT] **Session peak-hold** on attitude gauge (max |pitch|, |roll| since record start) — store peaks in session metadata / `extrasJson`
- ✅ [AGENT] Unit tests: known yaw + velocity heading → expected β; attitude ball mapping fixtures; low-speed β suppressed below threshold
- ✅ [AGENT] **`SensorPollScheduler`**: adaptive rates documented; phone-only defaults per `THERMAL_PERFORMANCE.md`
- ✅ [ADB] **Phone-only:** **ball tracks pitch/roll**, speed, numeric HDG, lateral G, **β plausible in turns** — `adb-smoke.ps1 -Sprint 3 -Scenario drift-simulation` on OnePlus 12 (`b5214fc6`)
- ✅ [ADB] 10-min recording thermal/CPU baseline + thermal banner smoke test — `adb-smoke.ps1 -Sprint 3 -Scenario thermal-recording` (30 s automated smoke; full 10 min manual per `THERMAL_PERFORMANCE.md`)

## Sprint 2 — Gauges + Calibration (2026-06-30)

- ✅ [AGENT] `docs/features/gauges.md` — acceptance criteria cite [`docs/design/GAUGE_REFERENCE.md`](docs/design/GAUGE_REFERENCE.md)
- ✅ [AGENT] Gauge colors + display typography in `design-tokens.json`; synced to `Color.kt` / `Type.kt` via `sync-design-tokens.py`
- ✅ [AGENT] `gauge/GaugeLogic.kt`, `AttitudeBallLogic.kt`, `GForceBallLogic.kt` stub + unit tests
- ✅ [AGENT] Canvas composables: `AttitudeGMeterGauge`, `SpeedometerGauge`, `HeadingReadout`, `TirePressurePanel`, `StatusIcons`, `GpsReadoutPanel`
- ✅ [AGENT] `CalibrationStore.kt` + **Calibrate / Set Level** on attitude panel (`GAUGE_REFERENCE.md`)
- ✅ [AGENT] `DashboardViewModel` wired to `TelemetryBus` + three-panel `DashboardHudLayout`
- ✅ [ADB] `adb-screenshot-compare.ps1 -Sprint 2` — device screenshot saved to `docs/design/gauge-reference/screenshots/sprint2-device.png`
- ✅ [ADB] `adb-smoke.ps1 -Sprint 2 -Scenario calibrate-level` on OnePlus 12 (`b5214fc6`)

## Sprint 1 — Foundation + ADR (2026-06-30)

- ✅ [AGENT] Draft ADR-0001, ADR-0003 (bundled); **`accept-adr.ps1 -Adr 0001,0003`**
- ✅ [AUTO] **`check-adr-gate.ps1 -Sprint 1`** before implementation tasks
- ✅ [AGENT] Automotive dark design tokens + day/night brightness + landscape shell
- ✅ [AGENT] Room + MapLibre + kotlin-obd-api dependencies (pinned, lockfile)
- ✅ [ADB] `adb-wait-device.ps1` + `adb-smoke.ps1 -Sprint 1 -Scenario cold-start` on OnePlus 12 (`b5214fc6`)

## Sprint 0 — Template Customization + Plan Materialization (2026-06-30)

- ✅ [AGENT] **`scripts/expedition/materialize-build-plan.ps1`** — write canonical `BUILD_PLAN.md` + `project.config.json` + `docs/START_HERE.md` + `docs/DEV_DEVICE.md` + `docs/RECOMMENDATIONS.md` + `.cursor/rules/expeditiongauge-plan.mdc`
- ✅ [AGENT] **`scripts/expedition/bootstrap.ps1 -Init`** — `init-project.ps1 -Stack android -ProjectName ExpeditionGauge -Prune -NonInteractive` → `setup-github-repo.ps1`
- ✅ [AGENT] **`scripts/expedition/sync-project-config.ps1`** — sync assets, INITIALIZATION_PROMPT placeholders, donations
- ✅ [AGENT] Rename package `dev.foss.goldenpath` → `dev.foss.expeditiongauge`; gauge reference assets
- ✅ [AGENT] Scaffold all **`scripts/expedition/*.ps1`** + `.github/workflows/verify-plan.yml`
- ✅ [AUTO] **`ensure-gh-auth.ps1`** inside bootstrap (blocker doc only if exit 2)
- ✅ [AUTO] **`scripts/expedition/sprint-signoff.ps1 -Sprint 0`** — validate-bootstrap, feature-gate, verify-plan-persisted, FOSS grep
- ✅ [AGENT] Pipeline unblock — fix `resume-agent.ps1` sprint-row matching; split oversized logic files; move UI composables under `ui/*/`; `file-limits` + `assembleDebug` + `testDebugUnitTest` green

## v0.11.0 release (2026-06-18)

- ✅ [HUMAN] Merge Release Please PR #14 — [v0.11.0](https://github.com/edwardlthompson/agent-project-bootstrap/releases/tag/v0.11.0) published
- ✅ [AGENT] Manual version sync on release PR branch (`5fe0fc1`) — Release Please extra-files gap
- ✅ [AUTO] CI + CodeQL + Security Scan green @ 6d4f4ac

## Sprint M29 — Post v0.11.0 release hardening (2026-06-18)

- ✅ [AGENT] Windows-safe `sync-template-version.sh` (quoted heredoc + env var; F-004)
- ✅ [AGENT] Auto-sync version files + SBOM dispatch in `release-please.yml` (F-001/F-002)
- ✅ [AGENT] Rename `health-check.yml` → `weekly-health-check.yml`; add `actions: read` (F-003)
- ✅ [AUTO] SBOM backfill workflow triggered for v0.11.0 (run `27731653800`)

## v0.10.0 release (2026-06-17)

- ✅ [HUMAN] `gh auth refresh -s security_events` (Dependabot API verified)
- ✅ [HUMAN] Merge Release Please PR #13 — [v0.10.0](https://github.com/edwardlthompson/agent-project-bootstrap/releases/tag/v0.10.0) published
- ✅ [AGENT] Sync `.template-version`, `TEMPLATE_INDEX.json`, README badge, `AGENT_MEMORY.md` to 0.10.0 (`36a02e4`)
- ✅ [AGENT] Fix `release.yml` SBOM backfill — checkout `main` when `tag` input set
- ✅ [AGENT] Add `sync-template-version.sh` + `check-template-version-sync.sh` gate
- ✅ [AGENT] `verify-fdroid-metadata.sh` green; no anti-features in template metadata
- ✅ [AUTO] Release workflow SBOM backfill — 7 assets on [v0.10.0](https://github.com/edwardlthompson/agent-project-bootstrap/releases/tag/v0.10.0) (run `27727807142`)
- ✅ [ADB] Device dry-run on CPH2583 (wireless ADB) @ 2026-06-18

## Sprint M28 — Weekly maintain audit (2026-06-18)

- ✅ [AGENT] Index + commit `fdroid-device-dry-run.{sh,ps1}` (CODE_REVIEW F-001/F-002)
- ✅ [AGENT] Fix `UpdateApplierTest` Robolectric FileProvider failure on Windows
- ✅ [AUTO] Security triage + CI green @ f78dd18; 0 Dependabot alerts/PRs
- ✅ [HUMAN] Merge Release Please PR #14 — superseded by v0.11.0 release (2026-06-18)

## BUILD_PLAN cleanup (2026-06-18, M28 complete)

- ✅ [AGENT] Archive M28; extend Archived Sprints row to M19–M28

## Sprint M5 — README Visual Refresh (2026-06-12)

- ✅ [AGENT] Harden `scripts/normalize-markdown-whitespace.py` — table-aware blank-line collapse
- ✅ [AGENT] Add `scripts/check-markdown-tables.sh`; hook into `validate-bootstrap.sh`
- ✅ [AGENT] Redesign README sections — shields.io badges + HTML `<dl>`/tables for What's Included, BUILD_PLAN Labels, Template Update Checker, Supported Stacks
- ✅ [AGENT] Add README badge conventions to `docs/MAINTAINING_THE_TEMPLATE.md`
- ✅ [AGENT] Run verification — encoding, design cohesion, markdown table lint, TEMPLATE_INDEX validation
- ✅ [HUMAN] Visual review on GitHub after push — badges load, links resolve *(closed M14: superseded by maintainer README cycles)*

## Template Maintainer — v0.2.1 Full Bootstrap Hardening (2026-06-13)

- ✅ [AGENT] Normalize `.gitignore` UTF-16 to UTF-8; extend encoding scan and pre-commit hook
- ✅ [AGENT] Sync `PROMPT_LIBRARY.md` entries 4, 6, 8, 9; populate `KNOWLEDGE_BASE.md` (6 entries)
- ✅ [AGENT] Document Lighthouse 3-run median in `modules/web/MODULE.md`
- ✅ [AGENT] SHA-pin `release.yml` actions; add pin policy to `docs/SECURITY_TRIAGE.md`
- ✅ [AGENT] Add `check-workflow-action-ref-format.sh` pre-commit hook
- ✅ [AGENT] Init scripts: `validate-workflow-actions` + `check-github-ci` reminder
- ✅ [AGENT] Devcontainer: encoding check, gh CLI feature, CI gate tip
- ✅ [AGENT] Add `health-check.yml` weekly workflow
- ✅ [AGENT] Bootstrap Gradle wrapper; CI `android-build` assembleDebug job
- ✅ [AGENT] Bump to v0.2.1; sync `TEMPLATE_INDEX.json`, `CHANGELOG.md`, `README.md`
- ✅ [HUMAN] Set GitHub About from `docs/GITHUB_ABOUT.md` (via `gh repo edit`)
- ✅ [HUMAN] Create GitHub Release tag `v0.2.1` (https://github.com/edwardlthompson/agent-project-bootstrap/releases/tag/v0.2.1)
- ✅ [HUMAN] GitHub settings: Dependabot alerts, private vulnerability reporting, branch protection (CI + Security Scan + CodeQL)
- ✅ [HUMAN] Replace `@[PROJECT_OWNER]` in CODEOWNERS with `@edwardlthompson` (template maintainer)

## Template Maintainer — v0.2.0 Backlog Fix (2026-06-12)

- ✅ [AGENT] Normalize UTF-16 files to UTF-8; add `scripts/check-file-encoding.sh` + CI + pre-commit
- ✅ [AGENT] Add `package-lock.json`, `uv.lock`, `.env.example`; expand `validate-bootstrap.sh`
- ✅ [AGENT] Sync `TEMPLATE_INDEX.json` with LICENSE, scripts, workflows, rules
- ✅ [AGENT] Sync README, SECURITY_TRIAGE, RUNBOOK, UPGRADING_FROM_TEMPLATE, PROMPT_LIBRARY, CHANGELOG
- ✅ [AGENT] Harden license-compliance CI; web coverage budget; android ops checklist
- ✅ [AGENT] Harden INITIALIZATION_PROMPT Sections 2/7/8 with Build Verification Gate
- ✅ [AGENT] Update BUILD_PLAN Sprint 0 + Milestone Gates
- ✅ [AGENT] Bump `.template-version` to 0.2.0; finalize CHANGELOG
- ✅ [HUMAN] GitHub settings: Dependabot alerts, private vulnerability reporting, branch protection, About
- ✅ [HUMAN] Replace `@[PROJECT_OWNER]` in CODEOWNERS with `@edwardlthompson`

## Template Maintainer — v0.6.0+ Web Layout & CI Fixes (2026-06-13)

- ✅ [AGENT] Add `docs/WEB_PROJECT_LAYOUT.md` and agent routing for docs/ vs examples/web/
- ✅ [AGENT] Localization scaffold docs (web `locales/` + Android `strings.xml`) separated from styles
- ✅ [AGENT] Android `NetworkStatusMonitor` for online/offline status parity with web
- ✅ [AGENT] Harden `check-design-cohesion` (CSS content guard, main.ts i18n, PS1 parity)
- ✅ [AUTO] CI, Security Scan, CodeQL, and GitHub Pages green on `main` (commit `38ce003`)
- ✅ [HUMAN] Enable GitHub Pages (Actions source) and workflow PR permissions via repo settings

## Sprint M0 — Template Hardening v0.2.2

- ✅ [AGENT] Add `scripts/setup-github-repo.sh` and `scripts/setup-github-repo.ps1` — idempotent Dependabot alerts, private vulnerability reporting, branch protection/rulesets (CI + Security Scan + CodeQL); print UI fallback checklist on API 422
- ✅ [AGENT] Add gitleaks CI job to `.github/workflows/security.yml` (or `ci.yml`) on PR + `main` push
- ✅ [AGENT] Add `check-file-limits` and `validate-bootstrap --quick` to `.pre-commit-config.yaml`
- ✅ [AGENT] Add `scripts/pre-release-gate.sh` and `scripts/pre-release-gate.ps1` — CI poll, Dependabot Critical/High count, template version/tag match, release dry-run reminder
- ✅ [AGENT] Add KNOWLEDGE_BASE KB-007 (npm/pip overrides policy for transitive CVEs); document `@lhci/cli` override in DECISION_LOG
- ✅ [AGENT] Add `npm audit` step to `examples/web` and `uv pip audit` (or equivalent) to weekly `.github/workflows/health-check.yml`
- ✅ [AGENT] Sync `AGENT_MEMORY.md` seed template version with `.template-version`; fix stale `0.1.0` reference
- ✅ [AGENT] Bump `.template-version` to `0.2.2`; update CHANGELOG, TEMPLATE_INDEX, README

## Sprint M1 — Template Hardening v0.3.0

- ✅ [AGENT] Extend `init-project.sh` / `.ps1` with interactive stack picker (web / python / android / multi / none) — prune unused `examples/` and `modules/`, never delete LICENSE/CI/scripts
- ✅ [AGENT] On init: sync `AGENT_MEMORY.md` active modules; emit minimal BUILD_PLAN Parallel section for chosen stack
- ✅ [AGENT] Add `.cursor-session-state.example.json` schema; document restore flow in `docs/FOR_AGENTS.md`
- ✅ [AGENT] Expand `docs/FOR_AGENTS.md` failure playbook (CI poll, GH_TOKEN, Dependabot conflicts, 3-strike escalation, parallel scope collision grep)
- ✅ [AGENT] Add `android-release` CI job — `SOURCE_DATE_EPOCH=1700000000 ./gradlew assembleRelease`, FOSS grep, optional two-run APK hash compare with flake tolerance
- ✅ [AGENT] Enforce `pytest --cov-fail-under=90` in CI for `examples/python`
- ✅ [AGENT] Add Conventional Commits PR title check (`amannn/action-semantic-pull-request`) to `.github/workflows/ci.yml`
- ✅ [AGENT] Draft `docs/adr/0001-core-architecture.md` pattern for child repos (MVVM / Clean / Hexagonal choice template)
- ✅ [AGENT] Bump `.template-version` to `0.3.0`; update CHANGELOG, TEMPLATE_INDEX, README

## Sprint M2 — Template Features v0.4.0

- ✅ [AGENT] Add `modules/node/MODULE.md` and `examples/node/` Golden Path stub (Fastify or Hono, MIT, typed, vitest)
- ✅ [AGENT] Add Node CI job to `.github/workflows/ci.yml` (lint, test, locked install)
- ✅ [AGENT] Add GitHub Pages deploy workflow for `examples/web` demo (FOSS, no tracking)
- ✅ [AGENT] Add Dependabot auto-merge workflow — patch/minor only, requires CI + dependency-review pass, excludes major without `[HUMAN]` label
- ✅ [AGENT] Add changelog automation (`release-please` or `git-cliff`) wired to Conventional Commits
- ✅ [AGENT] Add `scripts/simulate-template-upgrade.sh` — clone, init, cherry-pick per `docs/UPGRADING_FROM_TEMPLATE.md`, assert validate-bootstrap passes
- ✅ [AGENT] Add composite GitHub Action `action.yml` exporting `validate-bootstrap` for downstream repos
- ✅ [AGENT] Bump `.template-version` to `0.4.0`; update CHANGELOG, TEMPLATE_INDEX, README
- ✅ [AUTO] Upgrade simulation test passes in CI (optional scheduled job)
- ✅ [AGENT] GitHub Actions stale bot (`actions/stale`); exempt `template-improvement` (`.github/workflows/stale.yml`)
- ✅ [AGENT] PR coverage comment job (vitest + pytest artifacts; Codecov optional) (`.github/workflows/ci.yml`)
- ✅ [AGENT] `scripts/generate-winget-manifest.sh` stub generator (`packaging/winget/**`, `scripts/`)
- ✅ [AGENT] F-Droid `metadata/` template in `examples/android/` (`examples/android/metadata/**`)
- ✅ [AGENT] Per-stack SBOM slices on GitHub Release (`examples/web`, `examples/python`) (`.github/workflows/release.yml`)
- ✅ [AGENT] PROMPT_LIBRARY Entry 15 — Post-release regression (`PROMPT_LIBRARY.md`)
- ✅ [AGENT] PROMPT_LIBRARY Entry 16 — Template upgrade simulation (`PROMPT_LIBRARY.md`)
- ✅ [AGENT] Issue template: auto-suggest `.template-version` in placeholder text (`.github/ISSUE_TEMPLATE/*.yml`)

## Sprint M3 — Ecosystem Expansion v0.5.0+

- ✅ [AGENT] Add `examples/lightroom/` minimal stub (`Info.lua`, SDK version doc) per `modules/lightroom/MODULE.md`
- ✅ [AGENT] Update `TEMPLATE_INDEX.json` — set `examples/lightroom` module `example` path
- ✅ [AGENT] (Optional) Add `modules/rust/MODULE.md` + `examples/rust/` stub behind stack picker
- ✅ [AGENT] (Optional) Add `modules/go/MODULE.md` + `examples/go/` stub behind stack picker
- ✅ [AGENT] Gate new module CI behind workflow matrix `inputs.stack` or path filters to control CI minutes

## Sprint M4 — Design System v0.6.0

- ✅ [AGENT] Add `design-tokens/` + schema + `scripts/sync-design-tokens.py`
- ✅ [AGENT] Migrate Android example to Compose M3 + theme toggle (DataStore) + `strings.xml` i18n
- ✅ [AGENT] Refactor web example: CSS variables + theme toggle + `locales/` i18n scaffold
- ✅ [AGENT] Add `docs/DESIGN_GUIDE.md` + `.cursor/rules/design-system.mdc`
- ✅ [AGENT] Add `scripts/check-design-cohesion.sh` + validate-bootstrap wiring
- ✅ [AUTO] `android-build` + web tests green (theme toggle smoke tests)
- ✅ [AGENT] Web theme + i18n unit tests (`examples/web/src/theme.test.ts`, `examples/web/src/i18n/**`)
- ✅ [AGENT] Android Compose theme components (`examples/android/.../ui/**`)

## Milestone Gates

- ✅ [AUTO] Workflow action refs validated (`scripts/validate-workflow-actions.sh`)
- ✅ [AUTO] Pre-commit bare-semver guard (`scripts/check-workflow-action-ref-format.sh`)
- ✅ [AUTO] Android assembleDebug CI smoke on `examples/android/`
- ✅ [AUTO] Weekly health-check workflow polls CI + Security Scan + CodeQL
- ✅ [AUTO] UTF-8 encoding check clean (`scripts/check-file-encoding.sh`)
- ✅ [AUTO] Lockfiles present and CI uses locked installs (`npm ci`, `uv sync --locked`)
- ✅ [AUTO] `TEMPLATE_INDEX.json` complete (`scripts/validate-template-index.sh`)
- ✅ [AUTO] Gitleaks CI job passes on `main` (M0)
- ✅ [AUTO] Pre-commit includes file-limits and quick bootstrap validation (M0)
- ✅ [AUTO] Android `assembleRelease` with `SOURCE_DATE_EPOCH` passes (M1)
- ✅ [AUTO] Python coverage ≥ 90% in CI (M1)
- ✅ [AUTO] Web bundle size budget within threshold (M1)
- ✅ [AUTO] OpenSSF Scorecard run completed within last 30 days (M1)
- ✅ [AUTO] Upgrade simulation test passes (M2)
- ✅ [AUTO] GitHub Pages demo deploys successfully (M2)
- ✅ [AUTO] Node example CI green when `examples/node/` present (M2)
## BUILD_PLAN Automation Pass (2026-06-13)

### Sprint 0 — Template (maintainer repo complete)

- ✅ [AGENT] Create `SECURITY.md`, `CODE_OF_CONDUCT.md`, `docs/THREAT_MODEL.md`, `docs/PRIVACY.md`, `docs/RUNBOOK.md`
- ✅ [AGENT] Add `.github/CODEOWNERS` and `THIRD_PARTY_LICENSES.md`
- ✅ [AGENT] Initialize workspace memory files from template seeds (`AGENT_MEMORY.md`, etc.)
- ✅ [AGENT] Wire update checker config into devcontainer and README
- ✅ [HUMAN] Set GitHub repo About description from `docs/GITHUB_ABOUT.md` (via `gh repo edit`)
- ✅ [AGENT] Commit lockfiles (`package-lock.json`, `uv.lock`) and `.env.example`
- ✅ [AGENT] Ensure `TEMPLATE_INDEX.json` includes all scripts, workflows, and playbooks
- ✅ [AUTO] `scripts/check-file-encoding.sh` passes
- ✅ [AUTO] Full Build Verification Gate (INITIALIZATION_PROMPT Section 7) green
- ✅ [AUTO] `scripts/validate-bootstrap.sh` (expanded) passes in CI
- ✅ [HUMAN] Enable Dependabot alerts + security updates
- ✅ [HUMAN] Enable private vulnerability reporting + branch protection on `main` (via `setup-github-repo.sh`)
- ✅ [HUMAN] Replace `@[PROJECT_OWNER]` in CODEOWNERS with `@edwardlthompson`

### Sprint 0 Parallel (maintainer)

- ✅ [AGENT] Confirm GitHub Pages uses Actions (not `/docs` folder)
- ✅ [AUTO] Verify pre-commit hooks install

### Sprint 1 — Golden Path (maintainer)

- ✅ [AGENT] Propose directory structure for target stack
- ✅ [AGENT] Draft ADR-0001 core architecture (`docs/adr/0001-core-architecture.md`)
- ✅ [AGENT] Implement Golden Path reference feature (design tokens, i18n, theme toggle)
- ✅ [AUTO] `scripts/check-design-cohesion.sh` passes
- ✅ [AUTO] CI matrix green on main
- ✅ [AGENT] Web PWA offline cache + bundle budget + visual snapshots
- ✅ [AGENT] Python CLI + 90% coverage gate + pyright
- ✅ [AGENT] Android FOSS skeleton + Fastlane metadata stub
- ✅ [AGENT] Node API stub
- ✅ [AGENT] CodeQL + Trivy workflow wiring
- ✅ [AGENT] Devcontainer + pre-commit hooks

### Sprint M0 Parallel

- ✅ [AGENT] Cross-platform `scripts/check-file-encoding.py` (UTF-8/UTF-16 BOM)
- ✅ [AGENT] Add `.cursor/rules/windows-encoding.mdc`
- ✅ [AGENT] Add PROMPT_LIBRARY Entry 10 — Pre-release gate
- ✅ [AGENT] Add PROMPT_LIBRARY Entry 11 — GitHub repo setup
- ✅ [AGENT] Document setup script in `docs/SECURITY_TRIAGE.md` § Setup
- ✅ [AGENT] Wire `setup-github-repo` reminder into `init-project.sh` / `.ps1`
- ✅ [AUTO] Full Build Verification Gate + `scripts/pre-release-gate.sh` green on `main`

### Sprint M1 Parallel

- ✅ [AGENT] Web bundle size budget in CI (`scripts/check-bundle-size.sh`)
- ✅ [AGENT] Playwright visual snapshot regression test
- ✅ [AGENT] Service-worker offline smoke test
- ✅ [AGENT] Android Fastlane metadata stub
- ✅ [AGENT] Android emulator checklist in `examples/android/README.md`
- ✅ [AGENT] Optional pyright CI job for Python
- ✅ [AGENT] Add `.cursor/rules/testing.mdc` (coverage budgets)
- ✅ [AGENT] Add `.cursor/rules/ci-gates.mdc` (post-push poll protocol)
- ✅ [AGENT] PROMPT_LIBRARY Entry 12 — Stack prune complete
- ✅ [AGENT] PROMPT_LIBRARY Entry 13 — Session state restore
- ✅ [AGENT] PROMPT_LIBRARY Entry 14 — Parallel agent scope map
- ✅ [AGENT] OpenSSF Scorecard weekly workflow
- ✅ [AGENT] `scripts/check-parallel-scope.sh`
- ✅ [AUTO] CI matrix green including `android-release` and coverage gate
- ✅ [AGENT] Conventional Commits PR title check (`amannn/action-semantic-pull-request`)

### Sprint M3 Parallel

- ✅ [HUMAN] Decide which optional modules to ship — all three (Lightroom, Rust, Go); see `DECISION_LOG.md`
- ✅ [AGENT] Lightroom lint/checklist in CI (Lua SDK namespace grep)
- ✅ [AGENT] Rust CI job (`cargo fmt`, `clippy`, `test`)
- ✅ [AGENT] Go CI job (`go vet`, `gofmt`, `test`)
- ✅ [AGENT] F-Droid submission dry-run checklist doc (`modules/android/MODULE.md`)

### Milestone Gates

- ✅ [AUTO] Regression tests: zero failures
- ✅ [AUTO] Static analysis and vulnerability scans clean
- ✅ [AUTO] `scripts/pre-release-gate.sh` passes before release tag (M0)

## Sprint M7 — Incremental Feature Assembly + Agent Gates (2026-06-15)

- ✅ [AGENT] Add `docs/FEATURE_MODULES.md` and `.cursor/rules/feature-modules.mdc`
- ✅ [AGENT] Add `feature-gate.sh`, `feature-autofix.sh`, `agent-progress.sh`, `watch-agent-gates.sh`, `smoke-stack.sh` (+ `.ps1`)
- ✅ [AGENT] Extend session-state example, `ci-gates.mdc`, `testing.mdc`, `destructive-ops.mdc`; gitignore `agent-progress.json`
- ✅ [AGENT] Update BUILD_PLAN Sprint 2+ template, INITIALIZATION_PROMPT, FOR_AGENTS, PROMPT_LIBRARY Entry 17
- ✅ [AGENT] Harden agent handoff: `gates_passed`, `failed_stage`, `log_tail` in `agent-progress.sh`; `--step` forwarding
- ✅ [AGENT] Fix `watch-agent-gates.sh` JSON capture; scoped `--paths` autofix; `GATES_PASSED` subshell fix
- ✅ [AGENT] Add `FEATURE_MODULES.md` to `validate-bootstrap.sh`; cross-link `START_HERE.md`; node MODULE Feature gate section
- ✅ [AGENT] Integrate M7 closeout + Sprint M8 block into BUILD_PLAN.md

## Sprint M8 — Feature Gate CI Enforcement (2026-06-15)

- ✅ [AGENT] CI **Feature Gate** job with `--strict` multi-stack
- ✅ [AGENT] `pre-release-gate.sh` runs `feature-gate.sh`
- ✅ [AUTO] Branch protection includes Repo Hygiene + Feature Gate via `setup-github-repo.sh`
- ✅ [AUTO] `verify-about-feature-gate.sh`, `check-security-triage.sh`, `check-readme-health.sh`
- ✅ [AUTO] CI green on `810e259`; BUILD_PLAN HUMAN rows re-labeled to AGENT/AUTO where automatable

## Sprint M6 — Repo Hygiene Automation (2026-06-15)

- ✅ [AGENT] Add `.gitattributes`, `.editorconfig`, `.cursorignore`; expand `.gitignore`
- ✅ [AGENT] Add `check-tracked-artifacts`, `check-large-tracked-files`, `check-repo-hygiene`, `purge-ephemeral` scripts (+ `.ps1`)
- ✅ [AGENT] Wire repo-hygiene into pre-commit, `validate-bootstrap.sh`, and CI `repo-hygiene` job
- ✅ [AGENT] Add `docs/REPO_HYGIENE.md` and `.cursor/rules/repo-hygiene.mdc`
- ✅ [AUTO] CI **Repo Hygiene** job green after merge
- ✅ [AGENT] Archive Sprint M6 completions to `COMPLETED_TASKS.md`
- ✅ [AGENT] Index hygiene `.ps1` twins in `TEMPLATE_INDEX.json`

## Maintainer gate cycle (2026-06-15)

- ✅ [AUTO] `check-security-triage.sh --wait-ci 120` — zero Critical/High Dependabot; CI + Security Scan + CodeQL green on `f3013a0`
- ✅ [AUTO] `pre-release-gate.sh` — feature-gate, CI, Dependabot, `.template-version` 0.7.1
- ✅ [AUTO] `simulate-template-upgrade.sh` passed
- ✅ [AUTO] `run-maintainer-gates.sh --quick` — readme, fdroid metadata, feature-gate, CI jobs Repo Hygiene + Feature Gate
- ✅ [AUTO] `check-license-compliance.sh web` passed
- ✅ [AGENT] Fix Scorecard workflow job-level permissions (was failing publish_results)
- ✅ [AGENT] Add `docs/features/_template.md`, `docs/features/settings.md`, `verify-fdroid-metadata.sh`, `run-maintainer-gates.sh`
- ✅ [AGENT] F-Droid metadata scaffold: changelogs/1.txt, images/README.md
- ✅ [AUTO] Release Please PR #11 open (`chore(main): release 0.8.0`); pre-release gate green on `main`

## Sprint 2 starter scaffold (template maintainer, 2026-06-15)

- ✅ [AGENT] Feature acceptance template + Settings feature draft in `docs/features/`
- ✅ [AGENT] About screen remains Sprint 1 reference exemplar (not duplicated as Sprint 2 feature)
- ✅ [AGENT] BUILD_PLAN Sprint 2+ feature template rows indexed for child repos

## BUILD_PLAN cleanup (2026-06-15)

- ✅ [AGENT] Archive completed M5–M8 sprints; remove stale `✅` rows from active board
- ✅ [AGENT] Consolidate milestone gates into recurring pre-release + `run-maintainer-gates.sh`
- ✅ [AGENT] Split child-repo playbook from template-maintainer open items

## Code review → Sprint M9 integration (2026-06-15)

- ✅ [AGENT] Integrate 46 code-review findings into BUILD_PLAN Sprint M9 (Sequential + Parallel + Critique)
- ✅ [AGENT] Update PARALLEL_AGENT_SCOPES.md with M9 active scopes
- ✅ [AGENT] Reconcile child-repo Sprint 0 sign-off; simplify per-feature checklist; restore lane structure

## Sprint M9 — Sequential 1–7 (2026-06-15)

- ✅ [HUMAN/AGENT] Commit maintainer artifacts; scorecard fix; feature docs; metadata scaffold
- ✅ [AGENT] Fix 3-strike logic; `verify-agent-strikes.sh`
- ✅ [AGENT] `agent-progress.sh next --lane maintainer`; default `--step gate`
- ✅ [AGENT] `feature-gate.sh`: file-limits, python mypy/pyright; CI-only web gates documented
- ✅ [AGENT] Paginated Dependabot; `pre-release-gate` in `release.yml` workflow_dispatch
- ✅ [AGENT] TEMPLATE_INDEX bulk index + reverse validate-template-index scan
- ✅ [AGENT] About exemplar: AppShell refactor, Android UpdateStatusEvaluator, expanded about unit tests

## Sprint M9 — Sequential 8–12 + Parallel A–D (2026-06-15)

- ✅ [AGENT] Settings vertical slice per `docs/features/settings.md` (web + android containers, tests, i18n)
- ✅ [AGENT] Extend `check-file-limits.sh` for `.kt` Compose + `components/*.ts`; node in `init-project` stack picker
- ✅ [AGENT] Reconcile Sprint 0 sign-off across BUILD_PLAN, `INITIALIZATION_PROMPT.md`, `read-before-write.mdc`
- ✅ [AGENT] Scorecard in `check-security-triage.sh`; update `SECURITY.md`, `MAINTAINING_THE_TEMPLATE.md`, `START_HERE.md`, `FEATURE_MODULES.md`
- ✅ [AGENT] Module E/F renumbering; ADR-0000 template baseline; `security-triage.mdc`
- ✅ [AGENT] Parallel A: web settings slice + e2e smoke
- ✅ [AGENT] Parallel B: android settings slice + tests
- ✅ [AGENT] Parallel C: gate/CI hardening (file-limits, Scorecard triage)
- ✅ [AGENT] Parallel D: docs + rules + index (Node column, ADR-0000, security-triage.mdc)
- ✅ [AGENT] F-Droid image paths under `metadata/en-US/images/`; fdroiddata handoff in `modules/android/MODULE.md`

## BUILD_PLAN cleanup (2026-06-15, M9 closeout)

- ✅ [AGENT] Archive completed M9 AGENT rows; slim active board to release + distribution open items

## Sprint M11 — Post-M10 hardening (AGENT, 2026-06-15)

- ✅ [AGENT] Fix Android compile errors (`MainActivity` launch import; `GoldenPathApp` scope.launch)
- ✅ [AGENT] CodeQL java-kotlin: setup-java, Android SDK, Gradle assembleDebug before analyze
- ✅ [AGENT] ReleaseTagFetcher on Dispatchers.IO; offline + CheckSchedule gating; ReleaseAssetSelector wired
- ✅ [AGENT] release.yml: full pre-release on workflow_dispatch; lightweight tag gate (version + CI snapshot)
- ✅ [AGENT] Robolectric DataStore tests: ThemePreferencesTest, AppUpdatePreferencesTest
- ✅ [AGENT] About parity: clickable donations, no_compatible string, header nav toggle, BuildConfig.VERSION_NAME
- ✅ [AGENT] Web appBootstrap.ts composition root; settings.md wiring map updated
- ✅ [AGENT] Gate dedupe: run-maintainer-gates full mode uses pre-release only; check-github-ci --jobs
- ✅ [AGENT] Prune stale about.update.interval.* i18n; web e2e for update-check + About panel

## Sprint M12 — Post-M11 polish (AGENT, 2026-06-15)

- ✅ [AGENT] CodeQL Android: init before Gradle traced build
- ✅ [AGENT] Tag release gate `--wait 300 --jobs "Repo Hygiene,Feature Gate"`; `check-github-ci.ps1` `-Jobs`
- ✅ [AGENT] Robolectric DataStore isolation + `pendingRestart` test
- ✅ [AGENT] `ReleaseTagFetcherTest`, `DonationsLoaderTest`, `MainActivitySmokeTest`
- ✅ [AGENT] Web `appBootstrap.ts` vitest coverage + smoke tests
- ✅ [AGENT] Android `pendingRestart` UI stub in `GoldenPathApp`; DESIGN_GUIDE parity note
- ✅ [AGENT] Composition-root docs (`FEATURE_MODULES.md`, `feature-modules.mdc`, BUILD_PLAN Sprint 2)
- ✅ [AGENT] CHANGELOG M10/M11/M12; exemplar vs `.template-version` in MAINTAINING_THE_TEMPLATE; bug_report placeholder
- ✅ [AUTO] CodeQL workflow green on `main` after push (`7055255`)
- ✅ [HUMAN] Merge Release Please PR #11 after CodeQL + branch-protection checks green

## v0.9.0 release (2026-06-15)

- ✅ [HUMAN] Approve release tag; merge Release Please PR #12 (`chore(main): release 0.9.0`, `fd699bc`)
- ✅ [AUTO] Release Please published [v0.9.0](https://github.com/edwardlthompson/agent-project-bootstrap/releases/tag/v0.9.0)
- ✅ [AUTO] CI + Feature Gate + CodeQL green on `main` after merge (`fd699bc`)

## v0.8.0 release (2026-06-15)

- ✅ [HUMAN] Merge Release Please PR #11 (`chore(main): release 0.8.0`, `10b46d6`)
- ✅ [AUTO] CI + Feature Gate + CodeQL green on `main` after M12 (`7055255`)

## BUILD_PLAN cleanup (2026-06-15, M12 + v0.8.0 closeout)

- ✅ [AGENT] Archive M12 sprint body; slim active board to distribution + maintainer open items
- ✅ [AGENT] Unicode task markers (`🔲` / `✅` / `❌`) across BUILD_PLAN and checklist docs

## Sprint M13 — Human-gate automation (AGENT, 2026-06-15)

- ✅ [AGENT] `verify-branch-protection.sh` / `.ps1` — gh API compare vs `setup-github-repo.sh` defaults
- ✅ [AGENT] `init-project.sh` / `.ps1` `--stack`, `--prune`, and related CLI flags
- ✅ [AGENT] `verify-reproducible-apk.sh` / `.ps1` — local double-build hash check (CI parity)
- ✅ [AGENT] Wire branch-protection into `run-maintainer-gates.sh`; `TEMPLATE_INDEX.json` entries
- ✅ [AUTO] `verify-branch-protection.sh` green on template repo `main`

## Sprint M14 — Post-M13 review remediation (AGENT, 2026-06-15)

- ✅ [AGENT] P0 version coherence: `.template-version`, `TEMPLATE_INDEX.json`, `AGENT_MEMORY.md` → 0.8.0; manifest assert in `pre-release-gate.sh`
- ✅ [AGENT] P0 `init-project.ps1` `2>$null` fix; `-NonInteractive` + Python placeholder replacement in both init scripts
- ✅ [AGENT] P1 `run-maintainer-gates.sh`: `verify-reproducible-apk.sh` wiring, `--skip-apk`, unknown-flag fail, `--quick` docs
- ✅ [AGENT] P1 `verify-branch-protection.sh`: `strict` + `allow_force_pushes` asserts; rulesets note in `SECURITY_TRIAGE.md`
- ✅ [AGENT] P1 docs: `settings.md` in `TEMPLATE_INDEX.json`; reconcile `CHANGELOG.md` `[Unreleased]`; init CLI in `INITIALIZATION_PROMPT.md` §8
- ✅ [AGENT] P1 web: `AboutPanel.ts` DOM-safe donations; `APP_VERSION` via Vite `define`
- ✅ [AGENT] P1 Android: `check-file-limits.sh` GoldenPath UI roots; home-screen update status banner
- ✅ [AGENT] P1 CI: Android SBOM in `release.yml`; tag vs `workflow_dispatch` gate docs
- ✅ [AUTO] CI + Feature Gate green on `main` (`fc71433`)
- ✅ [HUMAN] Close stale M5 visual-review row (superseded by maintainer README cycles)
- ✅ [AGENT] Init next-steps numbering fixed in `.sh` / `.ps1`

## Sprint M15 — P2 backlog (AGENT, 2026-06-15)

- ✅ [AGENT] Init `--keep-optional` / `--prune-optional` for rust/go/lightroom when pruning
- ✅ [AGENT] CodeQL rust/go exclusion documented in `codeql.yml` + `modules/rust|go/MODULE.md`
- ✅ [AGENT] Playwright e2e: update check enabled → About status assertion
- ✅ [AGENT] `simulate-template-upgrade.sh` non-interactive init smoke
- ✅ [AGENT] `MainActivitySmokeTest` migrated to `ActivityScenarioRule`
- ✅ [AUTO] CI + Feature Gate green on `main` (`a5f3199`)
- ✅ [AGENT] `connectedDebugAndroidTest` CI job (`android-instrumented`); documented in `modules/android/MODULE.md`
- ✅ [AGENT] `release.yml` SBOM upload on `release` published + Release Please dispatch; tag push gate-only split
- ✅ [AUTO] CI + Feature Gate green on `main` (`5195c46`)
- ✅ [AGENT] SBOM backfill for v0.9.0; dispatch skip pre-release gate when `tag` input set

## Sprint M16 — Post-M15 code review (AGENT, 2026-06-15)

- ✅ [AGENT] P0 `--skip-workflows` on `check-github-ci.sh` / `.ps1`; tag-gate jobs-only poll in `release.yml`
- ✅ [AGENT] P0 SBOM tag ↔ `.template-version` assert; single checkout in `sbom-assets`
- ✅ [AGENT] P1 docs: `SECURITY_TRIAGE.md`, `MAINTAINING_THE_TEMPLATE.md`, `OPTIONAL_STACKS.md`
- ✅ [AGENT] P1 CI `path-changes` job; AOSP emulator target; `upgrade-simulation` gate enforced
- ✅ [AGENT] P1 BOM-less JSON writes in `init-project.ps1`; Playwright mocked update e2e
- ✅ [AGENT] P1 Release Please SBOM dedupe (`release` published only)
- ✅ [AUTO] CI + Feature Gate green on `main` (`f7213ec`, `7846d96`)
- ✅ [AGENT] P2 `--prune-optional` smoke in `simulate-template-upgrade.sh`; init flags docs
- ✅ [AGENT] P2 `AboutPanel` `aria-live="polite"`; `appBootstrap.test.ts` en.json strings
- ✅ [AGENT] Fix `examples/lightroom` removal on `--prune-optional` in init scripts

## Sprint M17 — Post-M16 code review (AGENT, 2026-06-15)

- ✅ [AGENT] P0 Android INTERNET permission + `ReleaseTagFetcherTest` (manifest + invalid-repo fetch)
- ✅ [AGENT] P0 Web update timing: `lastChecked` after successful fetch; unit tests for failure retry
- ✅ [AGENT] P0 Prune + template index: `init-stack-sync.py` prune index; simulate post-prune asserts
- ✅ [AGENT] P0 Release SBOM gate: `check-github-ci.sh --wait` on `release` published before SBOM
- ✅ [AGENT] P1 `check-github-ci.ps1` in-progress WAIT parity; `health-check.yml` `--wait 600`
- ✅ [AGENT] P1 `init-stack-sync`: emoji sync, rust/go MODULE_LINES, multi+prune `pruned` fix
- ✅ [AGENT] P1 Docs drift: INITIALIZATION_PROMPT step 5, Node in OPTIONAL_STACKS/README
- ✅ [AGENT] P1 FOSS grep: Kotlin/manifest/XML in `ci.yml`; path-changes android triggers
- ✅ [AGENT] P1 Pre-release: `check-license-compliance.sh`; manifest missing = FAIL
- ✅ [AGENT] Fix prune regression: `sync-design-tokens.py` + design cohesion stack-aware checks
- ✅ [AUTO] CI + Feature Gate green on `main` (`5d9be3e`)

## M17 P2 backlog (AGENT, 2026-06-15)

- ✅ [AGENT] Web modal a11y: `role="dialog"`, `aria-modal`, focus trap, Escape (`panelDialog.ts`)
- ✅ [AGENT] Wire `applyPwaUpdate()` in About panel; network-first SW; `UpdateApplierTest` for Android install boundary
- ✅ [AGENT] Config `.example` for web public + Android assets; stub `release_repo` in template
- ✅ [AGENT] `init-project.ps1` smoke in `simulate-template-upgrade.sh`; `ReleaseRepo` `Test-Path` guard
- ✅ [AGENT] Module letters E–G; `node` in `PARALLEL_AGENT_SCOPES.md`; index `MAINTAINING_THE_TEMPLATE.md`
- ✅ [AGENT] Android `GoldenPathUiTest` instrumented settings/about/theme assertions
- ✅ [AGENT] `checkForUpdates()` unit tests + axe e2e on open panels
- ✅ [AGENT] `android-release` CI strict reproducibility; rust/go SBOM slices in `release.yml`
- ✅ [AGENT] `health-check.yml` `uv sync --all-extras` for pip audit parity

## Sprint M18 — Post-P2 code review (AGENT, 2026-06-16)

- ✅ [AGENT] P0 Pages base path: `assetUrl()` helper; relative SW precache; BASE_URL-aware fetch/register
- ✅ [AGENT] P0 Web first paint: immediate `render()` in `appBootstrap.ts`; background update re-renders
- ✅ [AGENT] P0 Android apply slice: `ApkDownloadHelper`, `UpdateApplyCoordinator`, Apply button in About/home
- ✅ [AGENT] P0 Init config propagation: `sync-stack-config.py` wired in init scripts
- ✅ [AGENT] P1 Release SBOM guards: `hashFiles` conditionals for web/python/node/android; conditional upload
- ✅ [AGENT] P1 `init-stack-sync`: `active_modules` derived from filesystem via `MODULE_EXAMPLE_DIRS`
- ✅ [AGENT] P1 Release tag gate: full required-check poll on tag push
- ✅ [AGENT] P1 Repo hygiene: live config JSON gitignored; `sync-exemplar-config.sh`; tracked-artifact check
- ✅ [AGENT] P1 Go example: `go mod tidy` in CI; SBOM gated on `go.sum` (N/A for zero-dep stub)
- ✅ [AUTO] CI + Feature Gate green on `main` (`2721c01`)

## M18 P2 backlog (AGENT, 2026-06-16)

- ✅ [AGENT] `panelDialog.ts` unit tests (focus trap, Escape, focus restore)
- ✅ [AGENT] Playwright e2e for PWA apply + restart guard
- ✅ [AGENT] Web home update banner parity with Android
- ✅ [AGENT] `feature-gate.sh` design cohesion + about gate in strict multi
- ✅ [AGENT] Weekly Android instrumented smoke in `health-check.yml`
- ✅ [AGENT] KB-008 `android-release` strict hash policy documented
- ✅ [AGENT] `health-check.yml` simulate-template-upgrade step
- ✅ [AGENT] `run-maintainer-gates.sh` dedupe feature-gate in full mode
- ✅ [AGENT] `TEMPLATE_INDEX.json` roadmap + key exemplar paths
- ✅ [AGENT] SW `CACHE_NAME` stamped from package version at build
- ✅ [AGENT] `feature-gate.sh` rust/go smoke for multi strict
- ✅ [AGENT] `check-license-compliance.sh` rust/go slices
- ✅ [AUTO] CI + Feature Gate green on `main` (`d6b92a2`)

## Sprint M27 — Batch Instruction Templates (AGENT, 2026-06-17)

Slash commands + bare-word triggers for 25 batch workflows (20 atomic + 5 super).

- ✅ [AGENT] Create `.cursor/commands/*.md` (audit, debug, gates, triage, dependabot, push, prerelease, regress, feature, fix, init, prune, ci, docs, upgrade, setup, plan, restore, compact, scope + bootstrap, verify, build, ship, maintain)
- ✅ [AGENT] Add `.cursor/rules/batch-commands.mdc` (alwaysApply bare-word expansion)
- ✅ [AGENT] `docs/help/BATCH_COMMANDS.md` human cheat sheet; `docs/BATCH_COMMANDS.md` agent registry
- ✅ [AGENT] `CODE_REVIEW.md.example`, `RELEASE_NOTES.md.example`; gitignore ephemeral outputs
- ✅ [AGENT] `scripts/check-batch-commands.sh`; wire `validate-bootstrap.sh`, `simulate-template-upgrade.sh`, `TEMPLATE_INDEX.json`
- ✅ [AGENT] README Agent shortcuts; Child Playbook 2b; PROMPT_LIBRARY Entries 22–46; CURSOR_MODES batch row
- ✅ [AUTO] Validate: bootstrap --quick, template-index, feature-gate, check-batch-commands

## BUILD_PLAN cleanup (2026-06-17, M27 complete)

- ✅ [AGENT] Archive M27; extend Archived Sprints row to M19–M27

## Sprint M26 — Repo Sanity III (AGENT, 2026-06-17)

Post-commit review: TEMPLATE_INDEX drift, START_HERE path consistency, stale archive notes.

- ✅ [AGENT] Add `.cursor/rules/cursor-modes.mdc` to `TEMPLATE_INDEX.json` (bootstrap REQUIRED but unindexed)
- ✅ [AGENT] Align `START_HERE.md` repo-mode bullets with `docs/` paths
- ✅ [AGENT] Resolve stale M25 commit-blocker note in `COMPLETED_TASKS.md`
- ✅ [AUTO] Validate: bootstrap --quick, template-index, feature-gate, simulate-template-upgrade

**Deferred (no action):** CHANGELOG historical mojibake (`ΓÇö`) and legacy semver order — cosmetic; high diff noise.

## BUILD_PLAN cleanup (2026-06-17, M26 complete)

- ✅ [AGENT] Archive M26; extend Archived Sprints row to M19–M26

## Sprint M25 — Repo Sanity II (AGENT, 2026-06-17)

Post-M24 review: markdown table break, CHANGELOG ref, upgrade sim coverage.

- ✅ [AGENT] Fix `MAINTAINING_THE_TEMPLATE.md` table/heading blank line
- ✅ [AGENT] Retarget CHANGELOG historical Section 7 → 7a; extend `UPGRADING_FROM_TEMPLATE.md`
- ✅ [AGENT] Add CURSOR_MODES + changelog check to `simulate-template-upgrade.sh` AREAS
- ✅ [AUTO] Validate: bootstrap --quick, feature-gate (pass); simulate green after commit `9782e75`

## BUILD_PLAN cleanup (2026-06-17, M25 complete)

- ✅ [AGENT] Archive M25; extend Archived Sprints row to M19–M25

## Sprint M24 — Repo Sanity (AGENT, 2026-06-17)

Full-repo review: duplicate CHANGELOG [Unreleased], regression gate, init prompt sync.

- ✅ [AGENT] Remove duplicate CHANGELOG [Unreleased]; relocate M5 bullets to [0.5.0]
- ✅ [AGENT] Add `scripts/check-changelog-unreleased.sh`; wire validate-bootstrap + TEMPLATE_INDEX
- ✅ [AGENT] Sync INITIALIZATION_PROMPT §8 step 17 with CURSOR_MODES cross-link
- ✅ [AUTO] Validate: encoding, template-index, bootstrap --quick, feature-gate

**Deferred (no action):** CHANGELOG legacy semver order (0.5.0 before 0.2.2) and historical mojibake — cosmetic; batch normalize risks Release Please diffs.

## BUILD_PLAN cleanup (2026-06-17, M24 complete)

- ✅ [AGENT] Archive M24; extend Archived Sprints row to M19–M24

## Sprint M23 — Cursor Mode Closure (AGENT, 2026-06-17)

Upgrade guide, bootstrap gate for rule file, Debug links on gate failures, CHANGELOG.

- ✅ [AGENT] Add CURSOR_MODES + cursor-modes.mdc to UPGRADING_FROM_TEMPLATE cherry-pick table
- ✅ [AGENT] Link gate exit 2 / Failure Playbook to Debug Mode in feature-modules.mdc and FOR_AGENTS
- ✅ [AGENT] Add `.cursor/rules/cursor-modes.mdc` to validate-bootstrap.sh REQUIRED
- ✅ [AGENT] Document M19–M22 in CHANGELOG [Unreleased]; devcontainer CURSOR_MODES tip
- ✅ [AUTO] Validate: encoding, template-index, bootstrap --quick

## BUILD_PLAN cleanup (2026-06-17, M23 complete)

- ✅ [AGENT] Archive M23; extend Archived Sprints row to M19–M23

## Sprint M22 — Cursor Mode Consistency (AGENT, 2026-06-17)

Final pass: §7a reference drift, child playbook, session-restore prompts, maintainer safe-edit table.

- ✅ [AGENT] Retarget stale "Section 7" refs → §7a in INITIALIZATION_PROMPT, SECURITY_TRIAGE, THIRD_PARTY_LICENSES
- ✅ [AGENT] Align START_HERE repo-mode bullets + Child Playbook Sprint 0 step 2a with CURSOR_MODES
- ✅ [AGENT] Clarify repo vs Cursor mode in FOR_AGENTS Session Checkpoint + PROMPT_LIBRARY Entry 13
- ✅ [AGENT] Add CURSOR_MODES to MAINTAINING_THE_TEMPLATE safe-edit table; init step 2 README link
- ✅ [AGENT] Link 3-strike escalation to Debug Mode in FOR_AGENTS
- ✅ [AUTO] Validate: encoding, template-index, bootstrap --quick

## BUILD_PLAN cleanup (2026-06-17, M22 complete)

- ✅ [AGENT] Archive M22; consolidate M19–M22 in Archived Sprints table

## Sprint M21 — Cursor Mode Drift (AGENT, 2026-06-17)

Post-M20 review: init scripts, startup sequence, contributor docs, session-state schema, index entry_points.

- ✅ [AGENT] Sync `init-project.sh` / `init-project.ps1` next-steps prompt with CURSOR_MODES
- ✅ [AGENT] Add Cursor mode pick to `INITIALIZATION_PROMPT.md` §8 Startup Sequence (step 1a)
- ✅ [AGENT] Cross-link `docs/FEATURE_MODULES.md` and `CONTRIBUTING.md` to `docs/CURSOR_MODES.md`
- ✅ [AGENT] Clarify `.cursor-session-state.example.json` `mode` = repo mode; add `cursor_modes` to `TEMPLATE_INDEX.json` entry_points
- ✅ [AGENT] Align `core-directives.mdc` session-start line; note `cursor-modes.mdc` in README Cursor rules
- ✅ [AUTO] Validate: encoding, template-index, bootstrap --quick

## BUILD_PLAN cleanup (2026-06-17, M21 complete)

- ✅ [AGENT] Archive M21 sprint body; slim board to maintenance + human open items

## Sprint M20 — Cursor Mode Wiring (AGENT, 2026-06-17)

Post-M19 review: close prompt/read-order gaps and enforce CURSOR_MODES in bootstrap gate.

- ✅ [AGENT] Sync `START_HERE.md` agent prompts + Reference read order with `docs/CURSOR_MODES.md`
- ✅ [AGENT] Sync `PROMPT_LIBRARY.md` Entry 1/2 and `README.md` Quick Start bootstrap prompt
- ✅ [AGENT] Dedupe `INITIALIZATION_PROMPT.md` §6 Plan First → pointer to `docs/CURSOR_MODES.md`
- ✅ [AGENT] Update `AGENTS.md` Session Protocol; add `docs/CURSOR_MODES.md` to `validate-bootstrap.sh` REQUIRED
- ✅ [AGENT] Add `docs/CURSOR_MODES.md` to README What's Included; fix KB range in START_HERE
- ✅ [AUTO] Validate: encoding, template-index, bootstrap --quick

## BUILD_PLAN cleanup (2026-06-17, M20 complete)

- ✅ [AGENT] Archive M20 sprint body; slim board to maintenance + human open items

## Sprint M19 — Cursor Mode Routing (AGENT, 2026-06-17)

- ✅ [AGENT] Create `docs/CURSOR_MODES.md` (mode table, trivial rubric, transitions, prompt shortcuts; ≤80 lines)
- ✅ [AGENT] Create `.cursor/rules/cursor-modes.mdc` (`alwaysApply: true`; ≤30 lines; pointer to CURSOR_MODES)
- ✅ [AGENT] Wire entry points: `START_HERE.md`, `AGENTS.md`, `FOR_AGENTS.md`, `core-directives.mdc`, `TEMPLATE_INDEX.json`
- ✅ [AGENT] Split `INITIALIZATION_PROMPT.md` §7a (pre-release audit, Agent) vs §7b (defect investigation, Debug)
- ✅ [AGENT] Update `PROMPT_LIBRARY.md`: retitle Entry 3; add Entries 18–21 (Ask/Plan/Debug/Agent)
- ✅ [AUTO] Validate: `check-file-encoding.py`, `validate-template-index.sh`, `validate-bootstrap.sh --quick`

## BUILD_PLAN cleanup (2026-06-17, M19 complete)

- ✅ [AGENT] Archive M19 sprint body; slim board to maintenance + human open items

## BUILD_PLAN cleanup (2026-06-16, M18 P2 complete)

- ✅ [AGENT] Archive M18 sprint body; slim board to maintenance + human open items

## BUILD_PLAN cleanup (2026-06-16, M18 complete)

- ✅ [AGENT] Archive M18 sequential; slim board to P2 backlog + human open items

## BUILD_PLAN cleanup (2026-06-15, M17 P2 complete)

- ✅ [AGENT] P2 backlog: modal a11y, PWA apply wiring, config `.example`, PS1 smoke, docs/index, Android UI tests, release SBOM/reproducibility

## BUILD_PLAN cleanup (2026-06-15, M17 complete)

- ✅ [AGENT] Archive M17 sprint body; slim board to P2 backlog + human open items

## BUILD_PLAN cleanup (2026-06-15, M16 complete)

- ✅ [AGENT] Archive M16 sprint body; slim board to maintenance + human open items

## BUILD_PLAN cleanup (2026-06-15, M15 complete)

- ✅ [AGENT] Archive M15 sprint body; slim board to maintenance + human open items

## BUILD_PLAN cleanup (2026-06-15, M14 + v0.9.0 archive)

- ✅ [AGENT] Archive M14 sprint body; promote P2 to Sprint M15 active board
- ✅ [AGENT] Reset pre-release checklist for next version cycle

## Sprint M10 — Code review remediation (AGENT, 2026-06-15)

- ✅ [AGENT] M9-8 settings slice + parallel A–D committed; BUILD_PLAN cleanup
- ✅ [AGENT] Branch protection: export `GITHUB_REQUIRED_CHECKS` in `setup-github-repo.sh`; docs sync (5 checks)
- ✅ [AGENT] Node stack init: prune paths, `init-stack-sync.py` MODULE_LINES + PARALLEL_NOTES, INITIALIZATION_PROMPT Node row
- ✅ [AGENT] `check-security-triage.sh --strict`; Scorecard in `pre-release-gate.sh`; `SECURITY_TRIAGE.md` Scorecard section
- ✅ [AGENT] Gate parity: `--strict` in pre-release/maintainer gates; `pre-release-gate.sh` on tag push in `release.yml`
- ✅ [AGENT] Web settings fidelity: i18n, CSS, vitest coverage, cold-restart e2e, theme toggle sync
- ✅ [AGENT] Android settings fidelity: theme FilterChips, innerPadding, CheckSchedule tests
- ✅ [AGENT] Android About parity: DonationsLoader, ReleaseTagFetcher, GoldenPathApp composition root
- ✅ [AGENT] Opt-in update checks default `off`; About interval UI removed (Settings toggle only)
- ✅ [AGENT] CI/release: CodeQL java-kotlin, node SBOM + health-check audit
