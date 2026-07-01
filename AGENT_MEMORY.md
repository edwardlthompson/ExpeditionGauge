# Agent Memory

> Centralized index of tech stack, threat models, persistent context, and retrospectives.
> Update only at session startups, milestone boundaries, or major architectural pivots.

## Active Project: ExpeditionGauge

Child repo forked from agent-project-bootstrap (2026-06-30). **Shipped:** core v1 through **v2.11.7** (Relive wave + Dashboard HUD v2 + cube polish). **Dev device:** OnePlus 12 (`b5214fc6`).

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
| HUD cube polish | 2.11.3–2.11.8+ | ✅ Auto-record, calibrate, TPMS, telemetry, G-meter rotation |

## G-meter HUD rotation (locked 2026-06-30)

Portrait tile @ `ROTATION_0`: device pitch mirror + 90° CW **before** `rotateBall`. Landscape tile: post-remap per `displayRotation` (CCW @ 90, CW @ 270). Full matrix: `docs/design/GMETER_HUD_ROTATION.md`. Do not negate device roll (X) when fixing lateral pitch.

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
- **Template version:** `0.11.1` (see `.template-version`)
- **Last update check:** See `.template-update.json`
