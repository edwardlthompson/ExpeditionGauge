# Agent Memory

> Centralized index of tech stack, threat models, persistent context, and retrospectives.
> Update only at session startups, milestone boundaries, or major architectural pivots.

## Active Project: ExpeditionGauge

Child repo forked from agent-project-bootstrap (2026-06-30). **Shipped:** core v1 through **v2.18.10** (even HUD rows + Expedition APP throttle). **Dev devices:** OnePlus 13 (`8bf09993` primary), OnePlus 12 (`b5214fc6`).

| Milestone | Version | Status |
|-----------|---------|--------|
| Core v1 | 1.0.0 | ✅ Sprint 8 |
| Polish v1.1 | 1.1.0 | ✅ Sprint 14 |
| Polish v1.2 | 1.2.0 | ✅ Sprint 17b |
| v2 video | 2.0.0 | ✅ Sprint 18 |
| v2 live | 2.1.0 | ✅ Sprint 19 |
| Insets + orientation + Auto | 2.2.0–2.3.0 | ✅ Sprints 19b–21 |
| Relive wave | 2.4.0–2.9.0 | ✅ Sprints 22–27 |
| Post-audit hardening | 2.9.1 | ✅ Audit sprint 2026-06-30 |
| Dashboard HUD v2 | 2.10.0 | ✅ G-trail, drawer, storage loop, auto-record |
| HUD readability | 2.10.1 | ✅ Digital speed, units, MSL altitude, dark menus, TPMS grid |
| HUD cube layout v3 | 2.11.0 | ✅ Cube tiles, UnitDisplay, nav inset |
| HUD cube polish | 2.11.3–2.11.13 | ✅ Keep screen awake, reproducible release |
| AA grid + maps wave | 2.12.0 | ✅ GridTemplate HUD, imperial fix, route colors, offline prefetch |
| Inclinometer landscape | 2.14.0 | ✅ ADR-0013 SensorAxisRemap; styles; weekly-health stub |
| AA discovery + sideload | 2.14.1–2.14.2 | ✅ POI category; Play initiator install kit; HU/AAOS soft features + HEAD_UNIT_ROUTES |
| Audit 2026-07-12 | tooling | ✅ MSYS `/c`→Windows root; multi-strict optional skip; stack sync from project.config |
| Integer P/R/Y autocal | 2.15.0 | ✅ Whole-degree labels; still Zero + yaw offset; mag gate |
| Gauge cycle + elev DEM | 2.16.0 | ✅ Standalone gauges; G-meter axes; USGS DEM + A-GPS |
| AA GridItem crash + HU spec | 2.16.1 | ✅ Always setImage; AaDisplaySpec; local crash log |
| AA smoothness hardening | 2.16.2 | ✅ ActionStrip titles; async bridge; bitmap isolate; sensor hold |
| AA Play Store spoof kit | 2.16.3 | ✅ AA-install-kit.zip on Releases; aa-spoof-adb.sh; README copy-paste |
| AA Surface Drive HUD | 2.17.0 | ✅ NavTemplate Surface 3×1; DHU preview; Elev./stacked coords/TPMS 2-line |
| AA telemetry readability | 2.17.1 | ✅ Larger HDG/elev/coords; night contrast; vertical center; 16-pt cardinals |
| Sensor links / pairing / alert TTS / AA mute | 2.18.0 | ✅ Link row; wizards; Beep|TTS; Mute ActionStrip |
| TPMS QR wizard | 2.18.0 | ✅ FL→FR→RL→RR scan/manual + DataStore |
| AA OBD DTC footer | 2.18.0 | ✅ Mode 03 + OBDex CC0 carousel on ROW HUD |
| Bootstrap alignment | template 0.15.1 | ✅ FOSS Cursor surfaces + multicore validate; see `docs/BOOTSTRAP_ALIGNMENT.md` |
| Ext GPS + BT crash harden | 2.18.4 | ✅ GLO priority; socket IO catch; OBD icon bus merge |
| GLO stay-connected + pending DTCs | 2.18.5 | ✅ DataStore reconnect guard; Mode 07 AA footer |
| Driving-safe menus | 2.18.8 | ✅ Drawer root + Preset/Library pages; Settings hub |
| Donations and updates | 2.18.9 | ✅ Venmo About/Settings; once-per-version donate note; daily APK filename check |
| Even HUD rows + APP throttle | 2.18.10 | ✅ Seven centered cube rows; pedal in last row; 0149/Mode 22 pedal PIDs |

**Template lineage:** `.template-version` **0.15.1** (aligned 2026-07-22). App semver remains independent (Android `versionName` / Releases). Release Please automerge: **N/A** (RP job template-repo-only; ship via `create-release.ps1`).

## G-meter HUD rotation (locked 2026-07-12, supersedes 2026-06-30 CW cube)

Portrait tile: **identity** cube (roll→X, pitch→Y). Braking (−pitch) → ball toward front/top. Do not pitch-mirror or 90° CW in portrait. Landscape: post-remap per `displayRotation`. Full matrix: `docs/design/GMETER_HUD_ROTATION.md`.

## Elevation under weak GNSS (v2.16.0)

Prefer USGS 3DEP EPQS DEM when sats used &lt;6 or vertical accuracy &gt;15 m; A-GPS (XTRA/time + network) improves fix but does not supply elevation.

## Inclinometer landscape / IMU remap (locked 2026-07-09)

**ADR-0013.** Remap accel/gyro with `SensorAxisRemap` **before** Madgwick (screen-stable frame). Then locked portrait pitch↔roll swap. Activity `Display.rotation` is authoritative — never overwrite from Application `WindowManager` on the gyro path. Do **not** fix landscape with post-fusion Euler unwrap. Cursor rule: `.cursor/rules/inclinometer-rotation.mdc`. Tests: `SensorAxisRemapTest`, `VehicleAttitudeLogicTest`.

## Tech Stack (ExpeditionGauge)

| Layer | Technology | Notes |
|-------|-----------|-------|
| Platform | Android Compose + Room + MapLibre | `examples/android/` |
| Package | `dev.foss.expeditiongauge` | minSdk 24 |
| License | MIT | Pure FOSS, no Play Services in APK |

## Template Stack (maintainer reference)

| Layer | Technology | Version | Notes |
|-------|-----------|---------|-------|
| Platform | Multi-stack template (Web, Python, Android, Node, optional Lightroom/Rust/Go) | 0.11.1 | Template maintainer repo |
| License | MIT | - | Pure FOSS |
| Distribution | GitHub Releases + GitHub Pages demo | - | F-Droid/Winget stubs for child repos |

## Active Modules

- ✅ Web / PWA (`modules/web/MODULE.md`)
- ✅ Python (`modules/python/MODULE.md`)
- ✅ Android / F-Droid (`modules/android/MODULE.md`)
- ✅ Node API (`modules/node/MODULE.md`)
- ✅ Lightroom Classic (`modules/lightroom/MODULE.md`)
- ✅ Rust (`modules/rust/MODULE.md`)
- ✅ Go (`modules/go/MODULE.md`)

## Threat Model Checklist

- ✅ `docs/THREAT_MODEL.md` drafted (STRIDE, trust boundaries, top abuse cases)
- ✅ No proprietary closed-source SDKs in production path
- ✅ Opt-in only telemetry (GDPR/CCPA compliant); see `docs/PRIVACY.md`
- ✅ Secrets excluded from VCS (Gitleaks pre-commit)
- ✅ Dependency vulnerability scanning enabled (CodeQL + Trivy + Dependabot)
- ✅ Input validation at all data boundaries
- ✅ `SECURITY.md` and private vulnerability reporting enabled

## Persistent Context

### Project Purpose

FOSS Cursor agent bootstrap template: labeled BUILD_PLAN sprints, Golden Path examples, CI guardrails, workspace memory, and design-system cohesion across Web and Android.

### Key Constraints

- Max 300 lines per static data file (UI + i18n), 150 lines per pure logic file
- Trunk-based development with Conventional Commits
- Strict type safety and test coverage budgets

## Session Retrospectives

| Date | Milestone | What worked | What to improve |
|------|-----------|-------------|-----------------|
| 2026-06-13 | v0.6.0 design system | Cross-stack tokens + i18n scaffold | Restore optional-stack CI jobs after large merge |

## Template Provenance

- **Source template:** `edwardlthompson/agent-project-bootstrap` (self-maintained)
- **Template version:** `0.15.1` (see `.template-version`)
- **Last update check:** See `.template-update.json`
