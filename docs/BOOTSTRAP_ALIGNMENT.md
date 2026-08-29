# Bootstrap Alignment - ExpeditionGauge vs agent-project-bootstrap

> Living gap analysis and migration notes for aligning this child repo with upstream template **v1.0.0** (catch-up from 0.15.1).
> Local baseline at start of this pass: **0.11.1**. Do not treat as greenfield bootstrap.

**Upstream:** https://github.com/edwardlthompson/agent-project-bootstrap
**Stack:** Android FOSS (`project.config.json` -> `stack: android`)
**Product:** `examples/android/`  -  `dev.foss.expeditiongauge`

## Already matches (0.11.1 baseline)

- Agent entrypoints: `AGENTS.md`, `docs/START_HERE.md`, `docs/CURSOR_MODES.md`, `docs/FOR_AGENTS.md`
- `BUILD_PLAN.md` emoji status + Sequential/Parallel + `[AGENT]`/`[ADB]`/`[AUTO]` labels
- Memory/security: `AGENT_MEMORY.md`, `DECISION_LOG.md`, `KNOWLEDGE_BASE.md`, `SECURITY.md`, `docs/SECURITY_TRIAGE.md`, `docs/THREAT_MODEL.md`
- `.cursor/`: 26 batch commands, hooks, subagents, child rules (`expeditiongauge-plan`, `inclinometer-rotation`)
- Scripts: `scripts/agent-run.py`, validate/hygiene/gates, `scripts/expedition/*` (child-only)
- CI: 12 workflows including child-only `verify-plan.yml`
- Template checker config: `.template-update.json` (FOSS tier via `.cursor/stack-selection.json`)

## Missing vs 0.15.1 (closing this pass)

| Area | Item |
|------|------|
| Rules | `local-compute.mdc` |
| Skills | `canvas-bootstrap-status`, `check-repo-hygiene`, `feature-vertical-slice`, `sprint0-signoff` |
| Cursor | `worktrees.json`, `permissions.json`, `setup-worktree-unix.sh`, `setup-worktree-windows.ps1` |
| Scripts | `scripts/lib/run_checks_parallel.py` + validate-bootstrap multicore wiring |
| Docs | `docs/CURSOR_CLI.md`; CURSOR_INTEGRATIONS skill count; UPGRADING 0.15 rows |
| Plugin | `.cursor-plugin/` + `scripts/pack-cursor-plugin.*` |
| Version | `.template-version` 0.11.1 -> 0.15.1 (after gates) |

## Conflicts / careful merges

- Never overwrite `scripts/expedition/**`, product sources under `examples/android/`, or child rules
- Merge-only for `docs/START_HERE.md` / `docs/INITIALIZATION_PROMPT.md`
- CI cherry-pick by intent; keep `verify-plan.yml`
- Skip commercial docs and `commercial-compliance.mdc` (tier remains foss)

## Locked defaults

| Item | Decision |
|------|----------|
| `release-please-automerge.yml` | **N/A**  -  RP disabled for this child; Android release path |
| Commercial tier | Skip |
| FOSS 0.15 Cursor surfaces | Adopt |
| CI workflows | Surgical / allowlist only |
| Ephemeral APK/idsig | gitignored; optional purge |

## Risk register

| ID | Risk | Likelihood | Impact | Mitigation |
|----|------|------------|--------|------------|
| R1 | CI required-check breakage | Med | High | Allowlist-only CI; document skipped diffs; never delete `verify-plan.yml` |
| R2 | Release Please automerge unintended release | Low | High | Mitigated: RP job gated to template repo only; automerge not adopted (HUMAN closed N/A) |
| R3 | Hooks/permissions block expedition or quiet-shell | Med | Med | Diff denylist; fail-open hooks; smoke `check-cursor-hooks`; no denylist patterns matching `scripts/expedition` |
| R4 | `worktrees.json` missing setup scripts | High | Med | Adopt worktrees.json + both setup-worktree scripts as a set |
| R5 | Multicore validate thrashes Windows | Med | Med | `--quick` first; document `BOOTSTRAP_CHECK_JOBS=2` on Windows |
| R6 | UTF-16 corruption | Med | High | UTF-8 writes; `check-file-encoding` gate |
| R7 | Blind overwrite of child docs/product | Low | High | Path denylist; merge pointers only |
| R8 | Version bump before surfaces land | Med | Low | Bump `.template-version` only after S4 gates |
| R9 | TEMPLATE_INDEX / plugin drift | Med | Med | `check-cursor-integrations --tier foss` |
| R10 | Ephemeral APK clutter | Low | Low | Never stage `*.apk` / `*.idsig` / live `donations.json` |
| R11 | Parallel dispatch races ADB | Low | Med | Alignment does not run ADB/dispatch |
| R12 | Partial migration / 3-strike | Med | Med | Sequential rows; halt version bump on gate failure |

## Windows tip (R5)

```powershell
$env:BOOTSTRAP_CHECK_JOBS = "2"
python3 scripts/agent-run.py validate-bootstrap --quick
```

## Skipped CI diffs (S3 surgical review)

Compared shared workflows against upstream v0.15.1. **No workflow files were replaced** this pass (R1). Child `verify-plan.yml` kept.

| Workflow | vs upstream | Action |
|----------|-------------|--------|
| `ci.yml` | Differs (child longer; Android/product paths) | Skip mass replace |
| `codeql.yml` | Minor diff | Skip |
| `dependabot-automerge.yml` | Differs | Skip |
| `dependency-review.yml` | Differs | Skip |
| `pages.yml` | Minor diff | Skip |
| `release-please.yml` | Differs | Skip |
| `release.yml` | Differs | Skip |
| `scorecard.yml` | Differs | Skip |
| `security.yml` | Minor diff | Skip |
| `stale.yml` | Same | None |
| `weekly-health-check.yml` | Differs (child push stub) | Skip |
| `verify-plan.yml` | Child-only | Keep |
| `release-please-automerge.yml` | Upstream-only | Skip  -  N/A on child (R2 closed) |

Explicit skips:

- `.github/workflows/release-please-automerge.yml` - skipped; RP not active on ExpeditionGauge (R2)
- AGP/Kotlin pin changes from template - child pins authoritative
- Mass replace of `ci.yml` / `codeql.yml` / `weekly-health-check.yml`

## Migration notes

### Changed this pass

- Added `.cursor/rules/local-compute.mdc`, `.cursor/permissions.json`, `.cursor/worktrees.json` + setup-worktree scripts
- Added 4 skills (`canvas-bootstrap-status`, `check-repo-hygiene`, `feature-vertical-slice`, `sprint0-signoff`); refreshed existing 3
- Added `scripts/lib/run_checks_parallel.py` and wired `validate-bootstrap.sh` for multicore checks
- Added FOSS `.cursor-plugin/` + `scripts/pack-cursor-plugin.*`, `docs/CURSOR_CLI.md`, `docs/BOOTSTRAP_ALIGNMENT.md`
- Updated `AGENTS.md`, `docs/CURSOR_INTEGRATIONS.md`, `docs/START_HERE.md`, `docs/UPGRADING_FROM_TEMPLATE.md`, README agent section
- Merged `scripts/lib/check_cursor_integrations.py` (7 skills + permissions/worktrees; kept quiet-shell agent surface check)
- Surgical CI: documented skips only  -  no workflow file replacements; automerge deferred
- Bumped `.template-version` / manifest / `TEMPLATE_INDEX` / `.template-sync-sha` to **0.15.1**
- Gates green: encoding, hooks smoke, `validate-bootstrap --quick`, hygiene, cursor-integrations, watch-agent-gates

### Residual `[HUMAN]` / manual

1. ~~Evaluate Release Please automerge~~  -  closed **N/A** (RP template-only; ship via Gradle + `create-release.ps1`)
2. Optional: purge gitignored root `*.apk` / `*.idsig` via `purge-ephemeral`
3. Optional: full `validate-bootstrap` (non-quick) with `BOOTSTRAP_CHECK_JOBS=2`

### How agents should work (post-alignment)

1. Read `docs/START_HERE.md` -> pick Cursor mode via `docs/CURSOR_MODES.md`
2. Prefer local compute (`.cursor/rules/local-compute.mdc`) + `python3 scripts/agent-run.py ...`
3. Execute Sequential `[AGENT]` rows in `BUILD_PLAN.md` before Parallel
4. After each AGENT step: `python3 scripts/agent-run.py watch-agent-gates --once --autofix`
