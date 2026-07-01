# ExpeditionGauge — BUILD_PLAN

**Agent rule:** Finish all [AGENT] **Sequential** rows before **Parallel** dispatch. Shared schema/types stay Sequential-only. Details: [`docs/PARALLEL_AGENT_SCOPES.md`](docs/PARALLEL_AGENT_SCOPES.md), `scripts/plan-parallel-dispatch.sh`.

## Current state

- Android app: [`examples/android/`](examples/android/) · `dev.foss.expeditiongauge` · **v2.9.1** (`versionCode` 11).
- **Shipped:** core v1 (0–8), polish (9–17b), v2 video/live/insets/orientation/AA (18–21), Relive wave (22–27). Archive: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).
- **Post-audit hardening (2026-06-30):** complete — see board below.
- **Next:** Publish v2.9.1 draft release; optional F-Droid submit. Dependabot alerts enabled — zero open Critical/High.
- **Dev device:** OnePlus 12 · serial `b5214fc6` · [`docs/DEV_DEVICE.md`](docs/DEV_DEVICE.md).

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

> Archived @ 2026-06-30 → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## Sprint 1 — Foundation + ADR

> Archived @ 2026-06-30 → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

---

## Architecture decision (ADR-0001)

**Accepted ADRs:** 0001–0012.

| Layer | Choice |
|-------|--------|
| UI | Jetpack Compose + Canvas; MapLibre playback map (`maplibre-compose:0.13.0` pinned) |
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

---

## Active board — Post-audit hardening

| # | Owner | Task | Status |
|---|-------|------|--------|
| 1 | [AGENT] | `allowBackup=false` + finalize [`docs/PRIVACY.md`](docs/PRIVACY.md) (F-005) | ✅ |
| 2 | [AGENT] | `.trivyignore` for AGP test-harness Netty CVEs + KB-014 (F-007) | ✅ |
| 3 | [AGENT] | Export pipeline unit tests; split `adb-smoke.ps1` → `_adb-smoke-lib.ps1` + `adb-scenarios/relive.ps1` (F-008) | ✅ |

No open `[AGENT]` rows. New work: add rows here or in [`docs/ROADMAP.md`](docs/ROADMAP.md) deferred section.

---

## Key paths

| Area | Path |
|------|------|
| Entry | `MainActivity.kt`, `ui/ExpeditionGaugeApp.kt` |
| Fusion / recording | `fusion/`, `recording/`, `playback/` |
| Relive (22–27) | `media/`, `export/`, `flyover/`, `share/` |
| Layout | `ui/layout/InsetAwareScaffold.kt` |
| ADB smokes | `scripts/expedition/adb-smoke.ps1`, `adb-scenarios/relive.ps1` |

---

### Critique

**Strengths:** Single `TelemetryBus`; opt-in sprint toggles; Relive wave sequenced media → elevation → export → 3D → share.

**Risks (mitigated):**

| Risk | Mitigation |
|------|------------|
| MapLibre API drift | Pin `maplibre-compose:0.13.0`; [`docs/design/maplibre-3d-terrain.md`](docs/design/maplibre-3d-terrain.md) |
| Multi-BLE OEM variance | `BleConnectionBudget`; reconnect backoff in `ble/` |
| 3D flyover thermal | WorkManager + `FlyoverThermalGuard.kt`; clip/frame caps |
| Nav inset double-padding | `InsetAwareScaffold.kt` — one scaffold owner per route |
| Session backup leakage | `allowBackup=false`; local-only default |
| AGP Netty CVE noise | `.trivyignore` scoped to lockfile; revisit on AGP bump |

**Deferred:** Custom Canvas on Android Auto; AAOS standalone APK; real-time recording graphs; offline terrain packs → [`docs/ROADMAP.md`](docs/ROADMAP.md).

---

## Approval gate (automated)

Bootstrap complete (2026-06-30). All `project.config.json` sprint toggles through `v2_sharing_polish` are **on** and shipped.

After new rows: Agent Mode + `watch-agent-gates.sh --once --autofix` per [AGENT] step; `resume-agent.ps1` after Cursor reopen.
