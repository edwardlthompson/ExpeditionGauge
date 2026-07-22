# ExpeditionGauge — Agent Start Here

> Read this file first on every Cursor session. Canonical plan lives in git — never edit `.cursor/plans/`.

## Read order

1. [`BUILD_PLAN.md`](../BUILD_PLAN.md) — architecture + sprint board
2. [`project.config.json`](../project.config.json) — wave toggles, dev device, feature flags
3. [`docs/DEV_DEVICE.md`](DEV_DEVICE.md) — primary ADB hardware
4. [`docs/RECOMMENDATIONS.md`](RECOMMENDATIONS.md) — accepted stakeholder features
5. Active stack only: [`modules/android/MODULE.md`](../modules/android/MODULE.md), [`examples/android/`](../examples/android/)

## Resume workflow

```powershell
pwsh scripts/expedition/resume-agent.ps1
```

Execute the next `🔲 [AGENT]` row in BUILD_PLAN.md. After each step:

```powershell
python3 scripts/agent-run.py watch-agent-gates --once --autofix
```

On Windows, keep multicore bootstrap checks modest:

```powershell
$env:BOOTSTRAP_CHECK_JOBS = "2"
python3 scripts/agent-run.py validate-bootstrap --quick
```

Template alignment notes: [`BOOTSTRAP_ALIGNMENT.md`](BOOTSTRAP_ALIGNMENT.md). Mark completed rows via sprint sign-off or `mark-task.ps1`.

## Blockers {#blockers}

These are **not** BUILD_PLAN tasks. Scripts exit `2`; agent halts until resolved.

1. **GitHub credentials** — run the command printed by `ensure-gh-auth.ps1`, then re-run `bootstrap.ps1`.
2. **ADB device absent** — `[ADB]` rows need hardware. Run `pwsh scripts/expedition/adb-wait-device.ps1`. Primary dev device: OnePlus 12 (USB ADB, unlocked, rooted).
3. **Product judgment** — user edits `project.config.json` in chat → AGENT commits (one file only).

## Sprint 0 bootstrap (once)

```powershell
pwsh scripts/expedition/materialize-build-plan.ps1
pwsh scripts/expedition/bootstrap.ps1 -Init
pwsh scripts/expedition/sync-project-config.ps1
pwsh scripts/expedition/sprint-signoff.ps1 -Sprint 0
```