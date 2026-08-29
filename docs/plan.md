# Implementation Plan

> Active work lives in `BUILD_PLAN.md`. This stub satisfies bootstrap SDD presence.
> Status: 🔲 open · ✅ done · ❌ blocked.

## Milestone — Template catch-up (v1.0.0)

| Task | Owner | Tests / fallback |
|------|-------|------------------|
| ✅ Canon/Mixed template machinery | AGENT | `validate-bootstrap.sh --quick` |
| ✅ Golden Path catalog rows 1–7 added | AGENT | `BUILD_PLAN.md` Child Repo Playbook Sprint 31 |

## Next feature

Sprint 32 row 80: Live receiver local record (`/feature` live-receiver-record).

1. Copy `docs/features/_template.md` → `docs/features/{name}.md`
2. Lock the public API (Sequential)
3. Add unit tests with the implementation
4. Run `python3 scripts/agent-run.py watch-agent-gates --once --autofix`
