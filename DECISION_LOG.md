# Decision Log

> Chronological register of major technical trade-offs, accepted architectures, and rejected alternatives.
> **Treat past entries as immutable history; append only.**

## Format

```markdown
### YYYY-MM-DD — [Title]
- **Status:** Accepted | Rejected | Superseded
- **Context:** ...
- **Decision:** ...
- **Alternatives considered:** ...
- **Consequences:** ...

```

## Entries

_Seed template ADR: `docs/adr/0000-template-baseline.md`. Child repos use `docs/adr/0001-core-architecture.md`._

### 2026-06-20 — Repo-wide checklist status markers
- **Status:** Accepted
- **Context:** BUILD_PLAN and scattered checklists used mixed ⬜ / `- [ ]` / ✅ formats; inconsistent in Markdown Preview vs source
- **Decision:** Standardize on 🔲 open · ✅ done · ❌ blocked emoji markers repo-wide; document in `BUILD_PLAN.md` legend and agent read order
- **Alternatives considered:** GitHub `- [ ]` task lists (rejected: poor Preview readability and agent parsing); keep ⬜ white square (rejected: visually similar to ✅ in some fonts)
- **Consequences:** All new checklist rows use emoji; `agent-progress.sh` accepts legacy ⬜ for child repos during transition

### 2026-06-18 — Release automation hardening (M29)
- **Status:** Accepted
- **Context:** v0.11.0 release lacked SBOM assets (GITHUB_TOKEN cannot chain `release` → `release.yml`); Release Please skipped `extra-files`; `health-check.yml` registered as path name caused 0-job push failures
- **Decision:** `release-please.yml` runs `sync-template-version.sh` on release PR branches and dispatches `release.yml` on `release_created`; rename workflow to `weekly-health-check.yml`; fix sync script for Windows Git Bash
- **Alternatives considered:** PAT with workflow scope for release chaining (rejected: secrets management); manual SBOM backfill only (rejected: repeated human step each release)
- **Consequences:** Release Please needs `actions: write`; future releases should ship SBOM assets without manual dispatch

### 2026-06-17 — Batch instruction templates (M27)
- **Status:** Accepted
- **Context:** Agents and child-repo owners needed repeatable shortcuts for bootstrap, verify, build, ship, and maintenance workflows without re-pasting long prompts
- **Decision:** Ship 25 slash commands in `.cursor/commands/` (20 atomic + 5 super), bare-word expansion via `batch-commands.mdc`, human cheat sheet at `docs/help/BATCH_COMMANDS.md`, registry at `docs/BATCH_COMMANDS.md`; `/push` and `/ship` grant explicit push approval
- **Alternatives considered:** `beforeSubmitPrompt` hook for bare words (rejected: Cursor API cannot rewrite prompts); single mega-doc for humans and agents (rejected: overwhelms first-time users)
- **Consequences:** `alwaysApply` rule adds ~25 lines per session; `check-batch-commands.sh` prevents registry drift; child repos cherry-pick via `UPGRADING_FROM_TEMPLATE.md`

### 2026-06-13 — @lhci/cli npm overrides for transitive CVEs
- **Status:** Accepted
- **Context:** Lighthouse CI (`@lhci/cli`) bundles transitive dependencies (`tmp`, `uuid`) with known CVEs; no patched `@lhci/cli` release available at triage time
- **Decision:** Add npm `overrides` in `examples/web/package.json` forcing `tmp >= 0.2.6` and `uuid >= 11.1.1`; document in KB-007
- **Alternatives considered:** Dismiss Dependabot alert (rejected: hides real risk); remove Lighthouse CI job (rejected: loses performance gate)
- **Consequences:** Lockfile must be regenerated after override changes; overrides should be removed when `@lhci/cli` ships fixed dependencies

### 2026-06-13 — Ship all optional ecosystem modules (M3)
- **Status:** Accepted
- **Context:** Sprint M3 asked whether to ship Lightroom, Rust, and Go optional modules in the template maintainer repo
- **Decision:** Ship all three with Golden Path stubs, MODULE.md guides, and path-gated CI jobs (`lightroom`, `rust`, `go`) that skip when child repos remove the directories
- **Alternatives considered:** Lightroom-only (rejected: Rust/Go stubs are low-cost and popular); defer all optional modules (rejected: COMPLETED_TASKS M3 work already landed)
- **Consequences:** Template CI runs more jobs on `main`; child repos can delete unused `examples/` folders to skip jobs via `hashFiles` guards

### 2026-06-30 — Sprint 17b v1.2.0 release validation
- **Status:** Accepted
- **Context:** v1.2 release gate required reproducible APK, full ADB regression matrix, and polish wave 1–3 gates; playback ADB scenarios failed after Sprint 17 `RichSessionCard` UI change
- **Decision:** Ship v1.2.0 (`versionCode` 3) with verified reproducible hash via `clean assembleRelease --no-build-cache` + `SOURCE_DATE_EPOCH=1700000000`; fix `Open-PlaybackScreen` to tap **Play** and use `Test-PlaybackScreenOpen` markers; defer `create-release.ps1 -Tag v1.2.0` until `gh` auth + human approval
- **Alternatives considered:** Revert RichSessionCard tap target to date-only card (rejected: Play is the explicit affordance); skip playback ADB rows (rejected: regression matrix is release gate)
- **Consequences:** Windows reproducible builds require clean + `--no-build-cache`; GitHub tag/release remains manual follow-up

### 2026-06-30 — Sprint 18 v2.0.0 video + export
- **Status:** Accepted
- **Context:** v2 stretch (ADR-0005) required video sync, burn-in export, calibration wizard, developer mode, and enhanced bundles without breaking phone-only core path
- **Decision:** Ship v2.0.0 with ExoPlayer sync, MediaCodec burn-in, Room v3 video fields, `EnhancedExportService` ZIP bundles; gate via `check-v2-video-gate.sh`; defer GitHub release tag until human approval
- **Alternatives considered:** Keep VideoSyncEngine stub (rejected: BUILD_PLAN row 2–6 require functional slice); cloud transcode (rejected: privacy/offline-first)
- **Consequences:** DB migration uses destructive fallback on schema bump; Media3 added to dependency lockfile

### 2026-06-30 — Initial GitHub push + BUILD_PLAN trim (v2.1.0)
- **Status:** Accepted
- **Context:** Local repo had no `origin`; BUILD_PLAN exceeded 2300 lines; CI file-limit and design-cohesion gates failed on playback splits
- **Decision:** Trim BUILD_PLAN to active board + `COMPLETED_TASKS.md` archive; split 13 oversized files; add `playback` design tokens; index v2 gate scripts in `TEMPLATE_INDEX.json`; push to `edwardlthompson/ExpeditionGauge`
- **Alternatives considered:** Keep monolithic BUILD_PLAN (rejected: agent token cost); disable file-limit gate (rejected: CI policy)
- **Consequences:** Feature specs live in `docs/features/`; next sprint is 19b system UI insets

### 2026-06-30 — Sprint 19 v2.1.0 live telemetry
- **Status:** Accepted
- **Context:** Track-day pit crews need opt-in live metrics with FOSS-only stack and no cloud broker
- **Decision:** Ship v2.1.0 with OkHttp WebSocket signaling + stub metric relay through same server until FOSS WebRTC audited; QR pairing, in-app + web receivers, DataStore settings persistence; gate via `check-v2-live-gate.sh`
- **Alternatives considered:** `stream-webrtc-android` now (deferred: large binary, audit pending); raw WebSocket-only forever (rejected: no NAT traversal on cellular)
- **Consequences:** Interim transport relays metrics via signaling server (documented in ADR-0006 / live-telemetry.md); cellular/hotspot E2E remains manual two-device validation

### 2026-06-30 — Template main parity (post-v0.11.1, SHA 1cd2b72)
- **Status:** Accepted
- **Context:** Upstream `agent-project-bootstrap` `main` gained M30 Cursor hooks/skills/subagents, autonomous `/build` (`build_sprint.py`), and sprint automation scripts unreleased after tag v0.11.1
- **Decision:** Cherry-pick Tier-1 template infrastructure; hybrid-merge `/build` and `human_task_automation.py` with Expedition `scripts/expedition/*` ADB/AUTO delegates; retain expedition `FORBIDDEN_PATHS` in `parallel_scope.py`; keep `.template-version` at 0.11.1 until upstream v0.12.0; record SHA in `.template-sync-sha`
- **Alternatives considered:** Wait for v0.12.0 tag (rejected: user requested recent main updates); blind overwrite BUILD_PLAN/examples/android (rejected: product-specific)
- **Consequences:** `validate-bootstrap.sh` runs cursor hook/integration gates; orphan `parallel.md` removed; active sprints 23–27 marked `parallel_exception: sequential-only`

### 2026-06-30 — v2.9.0 Relive wave release
- **Status:** Accepted
- **Context:** Sprints 19b–27 completed on device (OnePlus 12); Relive-style export/share features ready for GitHub Release
- **Decision:** Ship `versionCode` 10 / `versionName` 2.9.0 with draft GitHub release; archive BUILD_PLAN Relive wave to `COMPLETED_TASKS.md`
- **Alternatives considered:** Staggered 2.2–2.9 point releases (rejected: single integrated Relive wave commit matches sprint archive)
- **Consequences:** `project.config.json` enables all v2 sprint toggles; F-Droid changelog `10.txt` added

### 2026-06-30 — Post-audit hardening v2.9.1
- **Status:** Accepted
- **Context:** `/audit` deferred F-005 (privacy/backup), F-007 (Trivy Netty), F-008 (export tests + adb-smoke split); BUILD_PLAN grew to ~280 lines with duplicate archive headers
- **Decision:** Ship `versionCode` 11 / `versionName` 2.9.1 with `allowBackup=false`, ExpeditionGauge `PRIVACY.md`, `.trivyignore` for AGP test-harness Netty only, export unit tests, adb-smoke modular split, BUILD_PLAN trim to ~120 lines
- **Alternatives considered:** AGP bump to resolve Netty transitives (deferred — lockfile churn); keep template README (rejected — child repo should describe the app)
- **Consequences:** Security Scan should pass; README reflects ExpeditionGauge; KB-014 documents Trivy ignore rationale

### 2026-06-30 — Dependabot triage: defer bulk Android bump
- **Status:** Accepted
- **Context:** Dependabot PR #4 proposed Kotlin 2.4, OkHttp 5.x, Gradle 9.6, and 21 dependency bumps; CI failed on `processDebugNavigationResources` and duplicate classes
- **Decision:** Close bulk PR; keep pinned FOSS stack on main. Apply github-actions group bump on main. Release Please disabled for ExpeditionGauge child repo (`if: github.repository == 'edwardlthompson/agent-project-bootstrap'`)
- **Alternatives considered:** Merge partial bumps only (deferred — needs dedicated regression pass); keep Release Please for template semver in child repo (rejected — conflicts with app v2.9.x releases)
- **Consequences:** Zero open Critical/High Dependabot alerts after Trivy ignore + alert enablement; android-dependencies group revisit in dedicated BUILD_PLAN row when AGP/Kotlin upgrade is scoped

### 2026-06-30 — Dashboard HUD v2.10.0
- **Status:** Accepted
- **Context:** User-requested HUD cleanup: G-trail, rotation-aware axes, portrait telemetry, minimal chrome, storage loop, BT auto-record
- **Decision:** Ship `versionCode` 12 / `versionName` 2.10.0 with `GaugeDisplayRotation`, drawer + top-bar Play/Stop, Room v6 protect/trigger columns, `SessionStorageBudget`, `AutoRecordMonitor`
- **Alternatives considered:** Static portrait axis swap (rejected — mount orientation varies); stop recording when storage full (rejected — dashcam loop deletes oldest unprotected)
- **Consequences:** ADB smokes use `record_play`/`record_stop`; destructive Room migration on dev upgrades; acceptance doc `docs/features/dashboard-hud-v2.md`

### 2026-06-30 — HUD readability v2.10.1
- **Status:** Accepted
- **Context:** OnePlus 12 field feedback after v2.10.0: imperial speed not wired, canvas speed arc cluttered, elevation below MSL, menus too bright, TPMS layout unclear
- **Decision:** Ship `versionCode` 13 / `versionName` 2.10.1 with digital-only speed, `speedUnit` through HUD, `AltitudeNormalizer` (API 34+ MSL + EGM96 fallback), `GaugeMenuSurface` dark chrome, TPMS 2×2 grid, whole-number pitch/roll
- **Alternatives considered:** Ship cube layout in same release (rejected — combined roadmap splits Phase A/B); router-level BackHandler (rejected — exceeds 300-line static gate)
- **Consequences:** 193 unit tests pass; nav-bar inset deferred to v2.11.0 cube work; combined HUD roadmap Phase B next

### 2026-06-30 — HUD cube layout v2.11.0
- **Status:** Accepted
- **Context:** Post–v2.10.1 feedback: cube 1:1 tiles, portrait pitch/roll axis bug, trail only in G modes, TPMS alignment, hardcoded km/h in alerts
- **Decision:** Ship `versionCode` 14 / `versionName` 2.11.0 with `ui/dashboard/hud/` cube framework, edge numerals, `UnitDisplay`, `TpmsPressureBands`, menu nav inset
- **Alternatives considered:** Keep row layout with borders only (rejected — inconsistent small-screen behavior); router-level calibrate button (rejected — detail sheet sufficient)
- **Consequences:** TWO_TILE fallback below 480dp portrait; portrait-only `isPortraitLayout` axis remap; settings/playback/live use unit-aware labels

### 2026-06-30 — Telemetry cube density v2.11.7
- **Status:** Accepted
- **Context:** OnePlus 12 feedback after v2.11.6: telemetry cube had dead vertical space; speed/HDG and coords felt small
- **Decision:** Ship `versionCode` 21 / `versionName` 2.11.7 with enlarged digits, `TelemetryHudMetaRow` (alt/time/GPS meta), attitude and OBD extras rows, `SpaceEvenly` cube layout
- **Alternatives considered:** Show lat G in cube (rejected — user removed in v2.11.5); hide alt/time during crawling recording in cube (rejected — wastes tile space)
- **Consequences:** `SpeedHeadingRow.enlarged`, `GpsReadoutPanel.hudCube`; GAUGE_REFERENCE center section updated

### 2026-06-30 — G-meter HUD rotation v2.11.9
- **Status:** Accepted
- **Context:** OnePlus 12 tuning: portrait cube validated at ROTATION_0; rotating to landscape or upside-down broke axes when cube remap ran after `rotateBall` or roll/pitch were confused
- **Decision:** Ship `versionCode` 23 / `versionName` 2.11.9 with `mapDeviceBallToHudScreen` pipeline (portrait cube in device space → `rotateBall` → landscape post-remap); `GMETER_HUD_ROTATION.md` as canonical contract
- **Alternatives considered:** Negate device roll X for lateral flip (rejected — flips screen Y after 90° CW); skip portrait 90° CW (rejected — validated portrait behavior)
- **Consequences:** Portrait locked: mirror pitch + CW before rotate; landscape CCW@90 / CW@270 / negate@180; tests in `GaugeDisplayRotationAllOrientationsTest`

### 2026-06-30 — EG monogram launcher icon v2.11.10
- **Status:** Accepted
- **Context:** Store/launcher used default Android icon; user selected Option C EG monogram for brand identity
- **Decision:** Canonical `docs/assets/app-icon-512.png`; `sync-app-icon.py` generates mipmaps + store assets; release APK with R8 minify + shrink (~26% smaller than debug)
- **Alternatives considered:** Option A G-meter bullseye (rejected — less distinctive); ship debug APK (rejected — user requested compression)
- **Consequences:** `AndroidManifest` `android:icon`; README centered icon; GitHub releases ship `assembleRelease` APK

### 2026-06-30 — Keep screen awake v2.11.13
- **Status:** Accepted
- **Context:** In-car HUD testing: system display timeout dimmed screen during drives
- **Decision:** `FLAG_KEEP_SCREEN_ON` via `ExpeditionGaugeTheme` SideEffect; `BrightnessPreferences.keepScreenAwake` default true; Settings toggle
- **Alternatives considered:** Partial wake lock (rejected — permission + battery); dashboard-only `Modifier.keepScreenOn()` (rejected — user chose full foreground)
- **Consequences:** Clears on background; no `WAKE_LOCK` permission; documented in GAUGE_REFERENCE + PRIVACY

### 2026-06-30 — v2.13.0 AA inclinometer + quiet agent shell
- **Status:** Accepted
- **Context:** Offroad/camper leveling needs ±45° inclinometer on AA and phone; agent shell commands opened `.sh` hook tabs and stole editor focus
- **Decision:** Shared `:car` `InclinometerBitmapRenderer`; `AttitudeGaugeMode.INCLINOMETER` + Offroad preset; AA Zero action; Python hooks + `agent-run.py` (KB-017)
- **Alternatives considered:** G-force on inclinometer tile (rejected); bash hook wrappers (rejected — focus steal); Mark on AA ActionStrip (removed — phone-only)
- **Consequences:** ADR-0010 revised; `AA_INCLINOMETER.md`; M-003 ADB/DHU checklist remains open post-ship

### 2026-06-30 — v2.12.0 AA grid, imperial, route colors, offline maps
- **Status:** Accepted
- **Context:** Drivers wanted head-unit HUD without opt-in toggle; imperial display gaps in playback; drift β route colors confused driving semantics; playback map online-only
- **Decision:** Always-on `GridTemplate` 3-tile AA (`TelemetryGridScreen`); `DrivingRouteStyling` lonAccel buckets; `MapTilePrefetchWorker` + home region + cellular prompt; imperial via `UnitDisplay` on graphs/overlays/elevation
- **Alternatives considered:** Pixel-perfect Compose HUD on AA (rejected — Car App Library policy); retain AA Settings toggle (rejected — user confirmed always-on); drift β as default route color (rejected)
- **Consequences:** ADR-0010/0011; `docs/help/ANDROID_AUTO.md`; demo MapLibre tiles for offline spike; ADB validation row M-003 remains open

### 2026-07-09 — Screen-stable IMU remap (inclinometer landscape)
- **Status:** Accepted
- **Context:** Portrait Zero + 90° CCW left the aviation horizon vertical; Application WM reported ROTATION_0 while Activity was ROTATION_90; Euler unwrap after Madgwick was gimbal-fragile
- **Decision:** `SensorAxisRemap` before Madgwick; Activity `Display.rotation` authoritative; Madgwick reset on rotation change; keep locked portrait pitch↔roll swap; one vehicle-frame Zero for all orientations (ADR-0013)
- **Alternatives considered:** Post-fusion Euler unwrap (rejected — singularity); re-zero per orientation (rejected — product requirement); Application WM on every gyro sample (rejected — OEM lie)
- **Consequences:** Landscape horizon stays level after portrait Zero; `.cursor/rules/inclinometer-rotation.mdc`; `SensorAxisRemapTest` guards the matrix
