# Contributing

Thank you for contributing to **ExpeditionGauge** — a FOSS Android HUD (package `dev.foss.expeditiongauge`).

## Who contributes what

| Label | Contributor | Examples |
|-------|-------------|----------|
| `AGENT` | Cursor Agent | Scaffolding, tests, CI config, docs |
| `HUMAN` | Human developer | Approvals, credentials, product decisions |
| `ADB` | Human (Android) | Device testing, F-Droid submission |
| `AUTO` | CI/scripts | GitHub Actions, Dependabot, pre-commit |

## For coding agents

Read [`AGENTS.md`](AGENTS.md) and [`docs/START_HERE.md`](docs/START_HERE.md) before editing. Run `/build` for the next Sequential row, then `python3 scripts/agent-run.py watch-agent-gates --once --autofix`. Do not `git push` unless a human approved it or the user invoked `/push` or `/ship`. Use Conventional Commits. Do not halt on `[HUMAN]` or `[ADB]` labels — automate first, then backlog.


## Getting started

1. Fork the repository and create a feature branch.
2. Read `docs/START_HERE.md`, `docs/CURSOR_MODES.md`, `CODE_OF_CONDUCT.md`, and `docs/MAINTAINING_THE_TEMPLATE.md`.
3. Report security issues via `SECURITY.md` (private reporting preferred).
4. Make changes; ensure CI passes locally where possible.
5. Open a PR using the provided template.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/).

## Template improvements

Use the **Template Improvement** issue template for feedback.

## Pre-commit hooks

```bash
pip install pre-commit
pre-commit install
pre-commit run --all-files
```

Includes repo hygiene checks (`scripts/check-repo-hygiene.sh`). See [`docs/REPO_HYGIENE.md`](docs/REPO_HYGIENE.md).

## Security triage

Maintainers run a weekly CVE triage pass per `docs/SECURITY_TRIAGE.md`. Review Dependabot alerts before each release.

## Release process (maintainers)

See `docs/MAINTAINING_THE_TEMPLATE.md` for the full semver release checklist.
