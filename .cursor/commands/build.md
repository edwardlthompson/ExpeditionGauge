# Autonomous sprint build — ExpeditionGauge hybrid

Execute the BUILD_PLAN **without asking the user questions, presenting options, or waiting for approval.** Pick the single best path internally (### Critique + ### Parallelization in your reasoning only), then implement it.

**Goal:** Complete as much of the active sprint as possible in one run. Run all `[AGENT]`/`[AUTO]` and Parallel work first, then attempt `[HUMAN]`/`[ADB]` rows via automation. Chain sprints until all actionable rows are done, 3-strike failure, or environment block.

## Rules

- **Never stop** to ask "which approach?" — decide and execute.
- **Never stop** for plan approval — `/build` is self-approving.
- **Never halt** on `[HUMAN]` or `[ADB]` labels — automate first; backlog only on failure.
- **Never stop** after a single feature row if the active sprint still has open work.
- **Do stop** only when: (a) 3-strike gate failure, (b) environment block (exit 2) after autofix, or (c) all actionable rows complete (`all_sprints_agent_auto_complete`).
- On gate failure: run @.cursor/commands/fix.md autonomously (up to 3 strikes) — do not suggest `/fix` and wait.
- **Parallel inline fallback:** if Task/CLI dispatch unavailable, execute every parallel scope in this session (never defer to a second `/build`).

## Step 0 — Load sprint state

```bash
bash scripts/build-sprint-status.sh --json --lane child

```

Optional Windows discovery:

```powershell
pwsh scripts/expedition/resume-agent.ps1 -NoDispatchParallel

```

Write `.cursor-session-state.json` fields: `active_sprint`, `build_plan_lane`, `autonomous_mode: true`.

If `all_sprints_agent_auto_complete: true` → print summary (include `HUMAN_BACKLOG.md` path if items exist) and exit.

## Step 1 — Sprint execution loop

Repeat until `sprint_agent_auto_complete`:

### 1a. Read status

```bash
bash scripts/build-sprint-status.sh --json --lane child

```

- If `next_row` is null and `sprint_agent_auto_complete` → go to Step 2 (sprint wrap-up).

### 1b. Execute `next_row`

| `next_row.action` | Action |
|-------------------|--------|
| `automate_human` / `automate_adb` | Run `bash scripts/attempt-build-plan-row.sh --owner "<owner>" --task "<task>" --sprint "<sprint>" --json`. On exit 0: mark row ✅. On exit 1: `bash scripts/build-backlog.sh add ...` and **continue** (row stays open). |
| `execute` | Implement the task; gate after each AGENT step; mark ✅ |
| `parallel_dispatch` | Run @.cursor/commands/scope.md fully, then `bash scripts/agent-progress.sh set-parallel-sprint-done --sprint "<sprint>"` |
| AUTO rows | `bash scripts/attempt-build-plan-row.sh` (Expedition: `check-v2-*-gate.sh`, `sprint-signoff.ps1`) or run scripts inline; mark ✅ on exit 0 |
**Expedition ADB:** automation routes to `pwsh scripts/expedition/adb-smoke.ps1 -Sprint <id> -Scenario <name>` when task contains a backtick scenario id.

**Expedition release:** when AUTO row names `create-release.ps1` and `ensure-gh-auth.ps1` exits 0, run release; skip push without `/ship`.

### 1c. Gate autofix (every AGENT step)

```bash
bash scripts/watch-agent-gates.sh --once --autofix

```

On Windows when bash reports `JAVA_HOME not set`:

```powershell
Push-Location examples/android; ./gradlew.bat :app:testDebugUnitTest --quiet; Pop-Location

```

Exit 1 → fix in scope and re-run (3-strike max). Exit 2 after 3 strikes → halt with evidence.

### 1d. Loop

Re-run `build-sprint-status.sh --json` and continue 1a.

## Step 2 — Sprint wrap-up

When `sprint_agent_auto_complete` for current sprint:

1. @.cursor/commands/gates.md — full local validation
2. @.cursor/commands/cleanup.md — archive ✅ rows to COMPLETED_TASKS.md
3. Print summary: sprint name, rows completed, backlogged items (`HUMAN_BACKLOG.md`)

## Step 3 — Chain to next sprint

Re-run `bash scripts/build-sprint-status.sh --json`.

- If `next_row` exists → **go to Step 1** immediately (no user pause).
- If `all_sprints_agent_auto_complete` → print final summary.

## Progress logging

Log one line per completed row (owner + task + automated|implemented). User sees sprint summaries only at wrap-up and final exit.

Begin now. Do not ask the user anything until the loop exits.
