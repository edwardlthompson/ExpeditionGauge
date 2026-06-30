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

