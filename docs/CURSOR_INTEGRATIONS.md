# Cursor Integrations

Single source mapping Cursor features → repo artifacts → FOSS vs Commercial exposure.

## FOSS quick start (default)

After `scripts/init-project.sh --distribution-tier foss`:

| Layer | Artifact | Status |
|-------|----------|--------|
| Rules | `.cursor/rules/*.mdc` | Shipped (14) |
| Commands | `.cursor/commands/*.md` | Shipped (26) |
| Hooks | `.cursor/hooks.json` + `.cursor/hooks/` | Shipped |
| Skills | `.cursor/skills/` (3) | Shipped |
| Subagents | `.cursor/agents/` (3) | Shipped |
| Modes | `docs/CURSOR_MODES.md` | Shipped |
| Parallel `/scope` | `scripts/plan-parallel-dispatch.sh` | Shipped |
| Autonomous `/build` | `scripts/build-sprint-status.sh` | Shipped |
| GitHub MCP (optional) | Copy `.cursor/mcp.foss.example` → `.cursor/mcp.json` | Example |

Validation: `python3 scripts/agent-run.py check-cursor-integrations -- --tier foss`

## Commercial quick start

After `scripts/init-project.sh --distribution-tier commercial`:

- All FOSS layers above remain enabled
- `sync-cursor-features.py` activates `commercial-compliance.mdc` instead of `foss-compliance.mdc`
- Copy commercial examples per [`CURSOR_COMMERCIAL_ACTIVATION.md`](CURSOR_COMMERCIAL_ACTIVATION.md)

## Hooks

Enforcement complement to rules (M27 — no `beforeSubmitPrompt`):

| Event | Module | Behavior |
|-------|--------|----------|
| `sessionStart` | `session_start_context.py` | Stack/tier one-liner |
| `beforeShellExecution` | `before_shell_guard.py` | Denylist + session `destructive_ops_approved` |
| `afterFileEdit` | `after_edit_encoding.py` | UTF-8 check, fail-open |
| `subagentStart` | `subagent_scope_inject.py` | Parallel lock scope |
| `beforeMCPExecution` | `mcp_audit.py` | Append audit log only |

**Opt-out:** `<!-- cursor-hooks: off -->` in `BUILD_PLAN.md`

**Session override:** `/push` and `/ship` set `destructive_ops_approved: ["git push"]` via `/compact`.

Validate: `python3 scripts/agent-run.py check-cursor-hooks -- --smoke`

## Quiet agent shell (KB-017)

Agent shell commands should use `python3 scripts/agent-run.py <name>` instead of `bash scripts/<name>.sh` so Cursor does not open hook or script tabs and steal editor focus.

Workspace relief: `.vscode/settings.json` sets `workbench.editor.revealIfOpen` and `autoReveal` to `false`. Pin the tab you are editing during agent runs.

```bash
python3 scripts/agent-run.py watch-agent-gates --once --autofix
python3 scripts/agent-run.py validate-bootstrap --quick
python3 scripts/agent-run.py --list
```

Underlying `scripts/*.sh` remain for CI and human Git Bash use; only **agent command strings** use the runner.

## Skills (progressive load)

Commands remain canonical UX. Skills wrap high-churn flows:

| Skill | Command |
|-------|---------|
| `validate-bootstrap` | `/gates` |
| `parallel-scope` | `/scope` |
| `watch-gates-autofix` | `/fix` |

## Subagents

| Agent | Role |
|-------|------|
| `verifier` | Readonly post-row gate check |
| `gate-fixer` | Scoped autofix for Parallel dispatch |
| `explorer` | Readonly codebase search (Plan Mode) |

## MCP activation (FOSS)

1. Copy `.cursor/mcp.foss.example` → `.cursor/mcp.json` (gitignored)
2. Set `GITHUB_TOKEN` in environment
3. Restart Cursor
4. Never commit tokens or live `mcp.json`

## Feature radar (maintainer)

- Registry: [`CURSOR_FEATURE_REGISTRY.json`](CURSOR_FEATURE_REGISTRY.json)
- Rubric: [`CURSOR_FEATURE_RADAR.md`](CURSOR_FEATURE_RADAR.md)
- Script: `python3 scripts/agent-run.py cursor-feature-radar`
- Outputs: gitignored `CURSOR_RADAR_REPORT.md`, `CURSOR_RADAR_BACKLOG.md`

## Switch tier later

```bash
python3 scripts/sync-cursor-features.py --tier foss|commercial
```

Requires `[HUMAN]` approval when swapping compliance rules.

## Cross-links

- [`docs/CURSOR_MODES.md`](CURSOR_MODES.md)
- [`docs/BATCH_COMMANDS.md`](BATCH_COMMANDS.md)
- [`docs/help/CURSOR_FEATURES.md`](help/CURSOR_FEATURES.md)
