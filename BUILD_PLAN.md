# ExpeditionGauge — BUILD_PLAN

**Agent rule:** Finish all [AGENT] **Sequential** rows before **Parallel** dispatch. Shared schema/types stay Sequential-only. Details: [`docs/PARALLEL_AGENT_SCOPES.md`](docs/PARALLEL_AGENT_SCOPES.md), `scripts/plan-parallel-dispatch.sh`.

## Current state

- Android app: [`examples/android/`](examples/android/) · `dev.foss.expeditiongauge` · **v2.18.12** (2026-08-28).
- **Shipped:** through **v2.18.12** (OBD DTC scan on connect). Audit 2026-07-29 archived.
- **Next:** Sprint 32 product backlog (🔲 `/allideas` rows; Sequential `/feature` one at a time).
- **Audit 2026-07-29:** gates green; Dependabot zero open Critical/High; CodeQL/CI/Security green on main; AGP/Kotlin automerge held (KB-026).
- **Dev device:** OnePlus 13 · serial `8bf09993` (primary); OP12 `b5214fc6` alternate — [`docs/DEV_DEVICE.md`](docs/DEV_DEVICE.md).

---

## Plan persistence

| File | Purpose |
|------|---------|
| [`BUILD_PLAN.md`](BUILD_PLAN.md) | Active board — **edit here only** |
| [`project.config.json`](project.config.json) | Sprint toggles, release repo |
| [`docs/START_HERE.md`](docs/START_HERE.md) | Agent cold-start |
After Cursor reopen: `pwsh scripts/expedition/resume-agent.ps1`. CI: `verify-plan-persisted.ps1`.

---

## Automation-first: zero `[HUMAN]` rows

Numbered rows use `[AGENT]`, `[AUTO]`, or `[ADB]` only. Human gates (gh auth, physical AA head unit) are **blockers**, not plan rows — see `ensure-gh-auth.ps1`, `adb-wait-device.ps1`, [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

Script catalog: [`scripts/expedition/`](scripts/expedition/) · toggles: `project.config.json` → `sprints.*` (all waves through 27 enabled).

---

## Sprint 0 — Template Customization + Plan Materialization

<!-- parallel_exception: archived -->
> Archived @ 2026-06-30 → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## Sprint 1 — Foundation + ADR

<!-- parallel_exception: archived -->
> Archived @ 2026-06-30 → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

---

## Architecture decision (ADR-0001)

**Accepted ADRs:** 0001–0013 (incl. 0010 AA grid, 0011 offline tiles, **0013 screen-stable IMU remap**).

| Layer | Choice |
|-------|--------|
| UI | Jetpack Compose + Canvas; MapLibre playback map (`maplibre-compose:0.13.1` pinned) |
| State | ViewModels + DataStore; Room sessions |
| Sensors | `TelemetryBus` — single fusion pipeline |
| BLE | `BleConnectionBudget` caps concurrent GATT |
| Privacy | Local default; live telemetry opt-in P2P; `allowBackup=false` |
| FOSS | No Play Services / Firebase in APK |
Deep dives: [`docs/design/`](docs/design/) · [`docs/adr/`](docs/adr/) · [`docs/features/`](docs/features/).

---

## Archived sprints

| Range | Version | Archive |
|-------|---------|---------|
| 0–8 | v1 core | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| 9–17b | v1.1–v1.2 polish | same |
| 18–19 | v2.0–v2.1 live | same |
| 19b–21 | v2.1.1–v2.3 insets / orientation / AA | same |
| 22–27 | v2.4–v2.9 Relive wave | same |
| Audit 2026-06-30 | post v2.9.0 hardening | same |
| Audit 2026-06-30 | v2.12 readiness (M-001–M-002) | same |
| Audit 2026-06-30 | v2.13 risks sprint (B-001–B-003) | same |
| Audit 2026-07-04 | post v2.13.0 hardening (A-001–A-002) | same — archived @ pending push |
| Audit 2026-07-09 | tooling + Dependabot lockfile (A-001–A-003) | same |
| Audit 2026-07-09 evening | inclinometer landscape ADR-0013 (A-001–A-004) | same |
| Audit 2026-07-12 | MSYS paths + multi-strict + stack sync (A-001–A-004) | same |
| Dashboard HUD v2 | v2.10.0 G-trail, drawer, storage loop | same |
| AA HU UX + ADB trim | v2.17.0 Surface HUD + DHU M-003; M-004 dropped | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Audit 2026-07-19 | post v2.17 docs/release hygiene (A-001–A-005) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Bootstrap alignment 0.15.1 | template tooling (S0–S4 + R2 N/A) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Sprint 28–30 / v2.18.0 | Sensor links, TPMS QR, alert TTS, AA mute, OBD DTC footer | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Audit 2026-07-29 | post v2.18.0 hygiene + DTC rescan (A-001–A-005) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Hotfix v2.18.11 | AA HDG GNSS course (H-001–H-003) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Hotfix v2.18.12 | OBD DTC scan on connect (H-001–H-003) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
| Sprint 31 | Golden Path catch-up (template v1.0.0) | [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) |
---

## Active board

> **Hotfix v2.18.12** archived in COMPLETED_TASKS.md @ `/ship`.
> **Sprint 31** archived in COMPLETED_TASKS.md @ `/build`.

## Child Repo Playbook

### Sprint 31 — Golden Path catch-up (template v1.0.0)

<!-- parallel_exception: archived -->
> Archived @ 2026-08-29 → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

### Sprint 32 — Product backlog (`/allideas`)

<!-- parallel_exception: one /feature row at a time; Sequential only -->

Port one vertical slice per row into `dev.foss.expeditiongauge`. Add `docs/features/{slug}.md` with the slice. Do **not** copy template `examples/` over the app. Skip leftover HUMAN/ADB: AA Customize launcher (`HUMAN_BACKLOG.md`).

#### Sequential

1. 🔲 [AGENT] `/feature` phone-hud-dtc — Phone HUD DTC carousel (AA already shows codes)
2. 🔲 [AGENT] `/feature` obd-shift-light — OBD RPM shift-light / redline threshold
3. 🔲 [AGENT] `/feature` obd-temps-voltage — Coolant / oil / voltage cluster (parked/idle)
4. 🔲 [AGENT] `/feature` gear-estimate — Gear estimate from RPM + speed
5. 🔲 [AGENT] `/feature` hud-tile-layout — User-configurable HUD tile layout
6. 🔲 [AGENT] `/feature` colorblind-hud — Color-blind HUD palettes
7. 🔲 [AGENT] `/feature` ambient-autodim — Ambient lux auto-dim refine
8. 🔲 [AGENT] `/feature` haptic-alerts — Haptic over-limit alerts
9. 🔲 [AGENT] `/feature` parked-idle-dim — Parked idle dim / burn-in guard
10. 🔲 [AGENT] `/feature` traction-circle — Live traction circle (latG/lonG)
11. 🔲 [AGENT] `/feature` dtc-clear — Parked Mode 04 clear DTCs with confirm
12. 🔲 [AGENT] `/feature` freeze-frame — Mode 02 freeze frame
13. 🔲 [AGENT] `/feature` im-readiness — I/M readiness monitors
14. 🔲 [AGENT] `/feature` vin-last6 — Mode 09 VIN last-6 only
15. 🔲 [AGENT] `/feature` ford-mode22-catalog — Ford Mode 22 PID catalog expand
16. 🔲 [AGENT] `/feature` optional-boost-pids — Optional MAP / AFR / boost PIDs
17. 🔲 [AGENT] `/feature` pid-discovery — OBD PID discovery wizard
18. 🔲 [AGENT] `/feature` wifi-elm327 — Wi-Fi ELM327 (FOSS TCP, no Play)
19. 🔲 [AGENT] `/feature` aa-custom-canvas — Custom Canvas AA Drive HUD
20. 🔲 [AGENT] `/feature` aa-night-mode — AA night mode from car UI
21. 🔲 [AGENT] `/feature` aa-a11y-type — AA larger type / TalkBack
22. 🔲 [AGENT] `/feature` aa-inclinometer-audio — AA inclinometer alert audio route
23. 🔲 [AGENT] `/feature` aa-parked-dtc — AA parked DTC detail pane
24. 🔲 [AGENT] `/feature` aaos-standalone — AAOS standalone APK
25. 🔲 [AGENT] `/feature` aa-parked-library — AA parked session library
26. 🔲 [AGENT] `/feature` live-record-graphs — Real-time recording graphs
27. 🔲 [AGENT] `/feature` session-map-compare — Two-session map compare
28. 🔲 [AGENT] `/feature` gpx-ghost-import — GPX/FIT import for ghost
29. 🔲 [AGENT] `/feature` session-split-merge — Session split / merge
30. 🔲 [AGENT] `/feature` track-autodetect — Auto-detect track from GPS
31. 🔲 [AGENT] `/feature` playback-speed — Variable playback speed
32. 🔲 [AGENT] `/feature` lon-g-heatmap — Brake / accel (lonG) heatmap
33. 🔲 [AGENT] `/feature` cornering-histogram — Cornering G histogram
34. 🔲 [AGENT] `/feature` storage-autodelete — Storage budget + auto-delete
35. 🔲 [AGENT] `/feature` session-notes — Session notes
36. 🔲 [AGENT] `/feature` csv-columns — CSV column picker
37. 🔲 [AGENT] `/feature` dual-dashcam — Multi-file dashcam import
38. 🔲 [AGENT] `/feature` playback-gamepad — Keyboard / gamepad scrub
39. 🔲 [AGENT] `/feature` offline-tile-cache — Offline tile cache completion
40. 🔲 [AGENT] `/feature` osm-speed-limit — Offline OSM speed-limit overlay
41. 🔲 [AGENT] `/feature` offline-geocoder — Offline geocoder for session titles
42. 🔲 [AGENT] `/feature` terrain-toggle — Hillshade / terrain Settings toggle
43. 🔲 [AGENT] `/feature` webrtc-datachannel — FOSS WebRTC Data Channel (ADR-0006)
44. 🔲 [AGENT] `/feature` live-receiver-record — Live receiver local record
45. 🔲 [AGENT] `/feature` live-encrypt — Optional live payload encrypt
46. 🔲 [AGENT] `/feature` live-multi-receiver — Multi-receiver pit room
47. 🔲 [AGENT] `/feature` mag-hardiron — Mag hard-iron wizard
48. 🔲 [AGENT] `/feature` ble-battery — BLE device battery icons
49. 🔲 [AGENT] `/feature` spare-tpms — Spare / 5th TPMS
50. 🔲 [AGENT] `/feature` tpms-temp-comp — TPMS temperature compensation
51. 🔲 [AGENT] `/feature` gnss-dead-reckon — GNSS-drop dead reckoning
52. 🔲 [AGENT] `/feature` phone-imu-live — Second-phone IMU via live
53. 🔲 [AGENT] `/feature` vehicle-pid-maps — Per-vehicle PID maps
54. 🔲 [AGENT] `/feature` parked-autocal-dwell — Longer parked autocal dwell
55. 🔲 [AGENT] `/feature` locales-es-de-fr — First locales (es / de / fr)
56. 🔲 [AGENT] `/feature` i18n-layout-stress — Translation layout stress
57. 🔲 [AGENT] `/feature` talkback-feedback — TalkBack on feedback / About
58. 🔲 [AGENT] `/feature` aa-high-contrast — AA high-contrast tokens
59. 🔲 [AGENT] `/feature` fdroid-reproducible — F-Droid metadata + reproducible publish
60. 🔲 [AGENT] `/feature` settings-json-backup — Settings JSON backup/restore
61. 🔲 [AGENT] `/feature` encrypted-session-zip — Encrypted session ZIP
62. 🔲 [AGENT] `/feature` privacy-report-export — Settings privacy-report export
63. 🔲 [ADB] crash-review-smoke — Crash-review ADB smoke (opt-in persist)
64. 🔲 [AGENT] `/feature` in-app-whats-new — In-app What’s new after version bump
65. 🔲 [AGENT] `/feature` settings-search — Settings search
66. 🔲 [AGENT] `/feature` app-shortcuts — App shortcuts (Record / Library)
67. 🔲 [AGENT] `/feature` last-session-widget — Last-session home widget
68. 🔲 [AGENT] `/feature` predictive-back — Predictive back on remaining routes
69. 🔲 [AGENT] `/feature` storage-meter — Storage usage meter
70. 🔲 [AGENT] `/feature` saf-folder-picker — SAF folder picker polish
71. 🔲 [ADB] emulator-hud-smoke — Instrumented emulator HUD smoke
72. 🔲 [ADB] inclinometer-landscape-pack — Inclinometer landscape ADB pack (ADR-0013)
73. 🔲 [AGENT] `/feature` obd-reconnect-soak — OBD reconnect soak test
74. 🔲 [AGENT] `/feature` agp-kotlin-bump — AGP/Kotlin bump validation (KB-026)
75. 🔲 [AGENT] `/feature` compose-preferred-framerate — Compose preferredFrameRate scroll vote
76. 🔲 [AGENT] `/feature` mark-event-voice — Local voice note on mark event
77. 🔲 [AGENT] `/feature` photo-story-timeline — Relive photo story timeline
78. 🔲 [AGENT] `/feature` night-hud-palette — Night-only HUD palette
79. 🔲 [AGENT] `/feature` recording-preroll — Recording pre-roll buffer
80. 🔲 [AGENT] `/feature` alert-snooze — Per-threshold alert snooze
81. 🔲 [AGENT] `/feature` aa-parked-voice — AA parked-only voice Record/Stop
82. 🔲 [AGENT] `/feature` ghost-video-overlay — Lap vs ghost video overlay export
83. 🔲 [AGENT] `/feature` ford-mode22-temps — Ford Mode 22 trans temp / EGT PIDs
84. 🔲 [AGENT] `/feature` fdroid-antifeatures — F-Droid Anti-Features + Fastlane polish
85. 🔲 [AGENT] `/feature` drift-run-ranking — Drift score / run ranking in library
86. 🔲 [AGENT] `/feature` offroad-hold-bars — Offroad hold-to-peak pitch/roll bars
87. 🔲 [AGENT] `/feature` compass-cal-reminder — Compass calibration reminder after mag spike
88. 🔲 [AGENT] `/feature` nmea-log-export — NMEA raw log export
89. 🔲 [AGENT] `/feature` obd-trip-since-clear — OBD trip-since-clear monitors
90. 🔲 [AGENT] `/feature` battery-saver-record — Battery-saver recording profile
91. 🔲 [AGENT] `/feature` thermal-record-ui — Thermal recording throttle UI
92. 🔲 [AGENT] `/feature` foss-map-styles — Additional FOSS map styles
93. 🔲 [AGENT] `/feature` gpx-beta-extensions — GPX extensions for β / latG
94. 🔲 [AGENT] `/feature` share-to-files — Default share to Files (not social)
95. 🔲 [AGENT] `/feature` onboarding-v218 — Onboarding refresh for v2.18+ features
96. 🔲 [AGENT] `/feature` developer-pid-sniffer — Developer PID sniffer (opt-in)
97. 🔲 [AGENT] `/feature` ble-permission-rationale — BLE scan permission rationale polish
98. 🔲 [AGENT] `/feature` preset-alert-thresholds — Per-preset alert thresholds
99. 🔲 [AGENT] `/feature` playback-bookmarks — Playback bookmarks from mark events
100. 🔲 [AGENT] `/feature` sector-times-csv — Sector times CSV export
101. 🔲 [AGENT] `/feature` wet-tire-alerts — Wet/rain tire alert profile
102. 🔲 [AGENT] `/feature` crawl-hud-declutter — Crawl-mode HUD declutter
103. 🔲 [AGENT] `/feature` external-gps-rate — External GPS baud / update-rate settings
104. 🔲 [AGENT] `/feature` multi-ecu-headers — Multi-ECU OBD headers
105. 🔲 [AGENT] `/feature` trailer-tpms — Trailer / 5th-wheel TPMS profile
106. 🔲 [AGENT] `/feature` inclinometer-zero-profile — Inclinometer zero persist per vehicle
107. 🔲 [AGENT] `/feature` screenshot-exif-strip — Screenshot EXIF/GPS strip
108. 🔲 [AGENT] `/feature` library-search-favorites — Library search and favorites
109. 🔲 [AGENT] `/feature` relive-chapters — Relive chapter markers from mark events
110. 🔲 [AGENT] `/feature` ghost-sector-compare — Compare ghost by sector
111. 🔲 [AGENT] `/feature` alert-history-log — Local alert history log
112. 🔲 [AGENT] `/feature` settings-qr-transfer — Settings transfer via local QR
113. 🔲 [AGENT] `/feature` video-burnin-fields — Video burn-in extra fields
114. 🔲 [AGENT] `/feature` keep-awake-moving — Keep-awake only while moving
115. 🔲 [AGENT] `/feature` thermal-log-interval — Log interval auto by thermal
116. 🔲 [AGENT] `/feature` dtc-full-title — Phone HUD DTC tap for full OBDex title
117. 🔲 [AGENT] `/feature` fastlane-next-changelog — Fastlane changelog for next versionCode
118. 🔲 [AGENT] `/feature` about-oss-notices — License / OSS notice completeness in About
119. 🔲 [ADB] dhu-screenshot-ci — DHU screenshot smoke when head-unit CLI is present
120. 🔲 [AGENT] `/feature` mark-event-chapters-export — Mark-event chapter list in HTML share

---

## Key paths

| Area | Path |
|------|------|
| Entry | `MainActivity.kt`, `ui/ExpeditionGaugeApp.kt` |
| Fusion / recording | `fusion/`, `recording/`, `playback/` |
| Relive (22–27) | `media/`, `export/`, `flyover/`, `share/` |
| Layout | `ui/layout/InsetAwareScaffold.kt` |
| ADB smokes | `scripts/expedition/adb-smoke.ps1`, `adb-scenarios/relive.ps1`, `adb-scenarios/aa-inclinometer.ps1` |
---

### Critique

**Strengths:** Single `TelemetryBus`; opt-in sprint toggles; Relive wave sequenced media → elevation → export → 3D → share.

**Risks (mitigated):**

| Risk | Mitigation |
|------|------------|
| MapLibre API drift | Pin `maplibre-compose:0.13.1`; [`docs/design/maplibre-3d-terrain.md`](docs/design/maplibre-3d-terrain.md) |
| AGP/Kotlin Dependabot | Automerge skips `com.android.*` / `org.jetbrains.kotlin.*` (KB-026) |
| Multi-BLE OEM variance | `BleConnectionBudget`; reconnect backoff in `ble/` |
| 3D flyover thermal | WorkManager + `FlyoverThermalGuard.kt`; clip/frame caps |
| Nav inset double-padding | `InsetAwareScaffold.kt` — one scaffold owner per route |
| Session backup leakage | `allowBackup=false`; local-only default |
| AGP Netty CVE noise | `.trivyignore` scoped to lockfile; revisit on AGP bump |
| AGP BouncyCastle lockfile pins | `testImplementation` bcprov 1.80.2 + `.trivyignore` CVE-2025-14813 (KB-015); do not global `force` |
| Large uncommitted feature drift | Commit after ADB; split inclinometer vs cursor infra commits |
| Hooks broken mid-migration | `.py` hooks + `hooks.json` committed together; smoke via `check-cursor-hooks --smoke` |
| Agent doc drift reintroduces `.sh` | `check_cursor_integrations` gate on `.cursor/` agent surfaces |
| Inclinometer AA alert audio routing | Red frame on AA tile; phone audio may not route — [`ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md) |
| Inclinometer landscape after portrait Zero | ADR-0013 `SensorAxisRemap` before Madgwick; Activity `Display.rotation` authoritative — [`.cursor/rules/inclinometer-rotation.mdc`](.cursor/rules/inclinometer-rotation.mdc) |
| 2006 Expedition `0111` is plate, not APP | Discover `0149`/`014A`/`014B` then Ford Mode 22; logcat `throttlePid=` (KB-035) |
| DHU “Waiting for phone…” after sideload | Keep `adb root` until HU server is up; do not unroot then force-stop AA (KB-036) |
| JAVA_HOME unset / WSL bash (F-001) | Prefer Git Bash in `agent-run.py`; set `JAVA_HOME` to JDK 17 — [`DEV_DEVICE.md`](docs/DEV_DEVICE.md) |
| Dependabot gradle without lockfile (F-002) | Regenerate `app/gradle.lockfile` on bump PRs; see SECURITY_TRIAGE |
| weekly-health red on every push | Push stub job; full health on schedule/dispatch only |
**Boarded:** Custom Canvas AA, AAOS APK, live record graphs, and offline tile cache are Sprint 32 rows (not deferred). Remaining human/device: [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md).

---

## Approval gate (automated)

Bootstrap complete (2026-06-30). All `project.config.json` sprint toggles through `v2_sharing_polish` are **on** and shipped.

After new rows: Agent Mode + `python3 scripts/agent-run.py watch-agent-gates --once --autofix` per [AGENT] step; `resume-agent.ps1` after Cursor reopen.
