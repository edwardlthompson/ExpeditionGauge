# Parallel dispatch (manifest + auto Task launch)

> Skill: `.cursor/skills/parallel-scope/`

Read @docs/PARALLEL_AGENT_SCOPES.md and the active BUILD_PLAN Parallel table.

## 1. Preconditions

- Sequential lock steps for the active sprint are complete.
- Run:

```bash
bash scripts/check-parallel-scope.sh
bash scripts/plan-parallel-dispatch.sh --require-sequential-clear --json --feature <activeSprint>

```

If blockers include open Sequential items, finish them first.

## 2. Manifest and scope lock

```bash
bash scripts/plan-parallel-dispatch.sh --require-sequential-clear --write-lock --json --feature <activeSprint>

```

Optional hard isolation: `bash scripts/setup-agent-worktrees.sh`

Print **agent_count** in one line.

## 3. Auto-dispatch rules

**When invoked from `/build`:** if Task/CLI dispatch unavailable, **execute every parallel scope inline in this session**.

| agent_count | Action |
|-------------|--------|
| 0 | Run `--suggest`, expand Parallel table once, retry; if still 0, inline or escalate |
| 1 | Execute inline (no Task tool) |
| 2–8 | **One message, N concurrent Task calls** using subagent **`gate-fixer`** (`run_in_background: true`). For read-only exploration, prefer **`explorer`**. |
## 4. Subagent prompt template

Use **`.cursor/agents/gate-fixer.md`**. Each subagent must receive:

- Read `.cursor/parallel-scope-lock.json` — stay inside assigned `scope` only.
- **Forbidden paths:** `BUILD_PLAN.md`, `COMPLETED_TASKS.md`, `MainActivity.kt`, `ExpeditionGaugeApp.kt`, `AppScreenRouter.kt`, `InsetAwareScaffold.kt`, composition roots per `scripts/lib/parallel_scope.py`.
- Branch: `feature/agent-<slug>` from lock file.
- After work: `bash scripts/watch-agent-gates.sh --once --autofix`
- Report: `bash scripts/agent-progress.sh set-step --name tests`

## 5. CLI fallback (ExpeditionGauge)

```powershell
pwsh scripts/expedition/dispatch-parallel-agents.ps1 -Sprint <activeSprint> -Wait
pwsh scripts/expedition/merge-parallel-agents.ps1 -Sprint <activeSprint>

```

## 6. Orchestrator merge

1. Wait for all subagents to complete.
2. Resolve conflicts (sequential owner only).
3. Run `bash scripts/watch-agent-gates.sh --once --autofix`.
4. Mark Parallel rows ✅ in BUILD_PLAN (Parallel agents never edit BUILD_PLAN).
5. Delete `.cursor/parallel-scope-lock.json` when done.
6. If sprint fully ✅, read @.cursor/commands/cleanup.md.

Begin now.
