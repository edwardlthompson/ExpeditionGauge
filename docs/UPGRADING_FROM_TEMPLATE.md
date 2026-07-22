# Upgrading From Template

Child repos do not auto-sync with the upstream template. Use this guide when the update checker notifies you of a new release.

## Step 1: Read the Notification

Run `scripts/check-template-updates.sh` or check the devcontainer postStart output.

## Step 2: Review CHANGELOG

Read the upstream release notes at `github.com/edwardlthompson/agent-project-bootstrap/releases`.

## Step 3: Cherry-Pick by Area

| Changed area | Strategy | Owner |
|-------------|----------|-------|
| `.github/workflows/` | Cherry-pick or manual merge | AGENT + HUMAN review |
| `.cursor/rules/` | Copy new/changed `.mdc` files | AGENT |
| `docs/CURSOR_MODES.md` | Copy; canonical Cursor mode router | AGENT |
| `.cursor/rules/cursor-modes.mdc` | Copy with other rules | AGENT |
| `.cursor/commands/` | Copy all slash command files | AGENT |
| `.cursor/rules/batch-commands.mdc` | Copy with other rules | AGENT |
| `docs/help/BATCH_COMMANDS.md` | Copy human cheat sheet | AGENT |
| `docs/BATCH_COMMANDS.md` | Copy agent registry | AGENT |
| `CODE_REVIEW.md.example` | Copy audit template | AGENT |
| `RELEASE_NOTES.md.example` | Copy release draft template | AGENT |
| `scripts/check-batch-commands.sh` | Copy with validate-bootstrap | AGENT |
| `docs/INITIALIZATION_PROMPT.md` | Manual review; do not blind overwrite | HUMAN |
| `scripts/` | Copy updated scripts | AGENT |
| `scripts/check-file-encoding.sh` | Copy + add CI/pre-commit gate | AGENT |
| `scripts/validate-bootstrap.sh` | Copy expanded validation | AGENT |
| `scripts/check-changelog-unreleased.sh` | Copy with validate-bootstrap | AGENT |
| `scripts/check-license-compliance.sh` | Copy strict license gate | AGENT |
| `.github/workflows/dependency-review.yml` | Cherry-pick workflow | AGENT + HUMAN review |
| `.cursor/rules/destructive-ops.mdc` | Copy new rule file | AGENT |
| `.env.example` | Merge new vars; never overwrite local `.env` | AGENT |
| `LICENSE` | Verify MIT still applies | HUMAN |
| `examples/` | Reference only unless adopting new stack | HUMAN decision |
| `TEMPLATE_INDEX.json` | Run validate script after merge | AGENT |
| `.cursor/hooks/` + `hooks.json` | Copy FOSS hooks; review `shell-denylist.txt` for child scripts | AGENT |
| `.cursor/skills/` | Copy parallel-scope, validate-bootstrap, watch-gates-autofix | AGENT |
| `.cursor/agents/` | Copy explorer, gate-fixer, verifier subagents | AGENT |
| `scripts/build-sprint-status.sh` + `scripts/lib/build_sprint.py` | Copy; patch playbook marker for child BUILD_PLAN sections | AGENT |
| `scripts/lib/human_task_automation.py` | Copy; extend with child ADB/AUTO delegates | AGENT |
| `HUMAN_BACKLOG.md.example` | Copy; never overwrite live `HUMAN_BACKLOG.md` | AGENT |
| `scripts/expedition/*` | Child-only — do not overwrite from template | HUMAN |
| `examples/android/` (product app) | Child-only — reference GoldenPath patterns only | HUMAN |
| `.cursor/rules/local-compute.mdc` | Copy (0.15 local-first compute) | AGENT |
| `.cursor/permissions.json` | Copy; keep dual-layer with destructive-ops | AGENT |
| `.cursor/worktrees.json` + `setup-worktree-*.{sh,ps1}` | Copy as a set | AGENT |
| `.cursor/skills/*` (7 skills) | Copy new/changed SKILL.md | AGENT |
| `scripts/lib/run_checks_parallel.py` | Copy; wire via `validate-bootstrap.sh` | AGENT |
| `docs/CURSOR_CLI.md` | Copy | AGENT |
| `.cursor-plugin/` + `scripts/pack-cursor-plugin.*` | Copy FOSS plugin pack | AGENT |
| `.github/workflows/release-please-automerge.yml` | Skip on ExpeditionGauge — RP gated to template repo; Android ships via Gradle | HUMAN (closed N/A) |
| `docs/BOOTSTRAP_ALIGNMENT.md` | Child gap/risk log for this upgrade | AGENT |

See also: [`docs/BOOTSTRAP_ALIGNMENT.md`](BOOTSTRAP_ALIGNMENT.md).

## Version Compatibility

| Upgrade | Notes |
|---------|-------|
| 0.1.x → 0.1.y | Safe PATCH; cherry-pick freely |
| 0.1.x → 0.2.0 | Check CHANGELOG for new files/schema changes |
| 0.x → 1.0.0 | Full review; init prompt structure may have changed |
## Decision Points

- `[HUMAN]` Approve which upstream changes to adopt
- `[AGENT]` Apply diffs to matching files
- `[AUTO]` CI validates after merge
