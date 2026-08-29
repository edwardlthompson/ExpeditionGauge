# Feature: github-feedback

> Compose GitHub issue-form URLs, clipboard fallback, fail-soft duplicate search.

## Acceptance criteria

- 🔲 Small fields prefill `issues/new?template=...`; large bodies use clipboard + short URL
- 🔲 Placeholder `OWNER/REPO` never hits the network; 403/timeout return empty search
- 🔲 Open GitHub is `https` only
- 🔲 Default feedback repo is `edwardlthompson/ExpeditionGauge`

## Smoke scenario

1. Given `release_repo` `edwardlthompson/ExpeditionGauge` and fingerprint `a1b2c3d4e5f6`
2. When the composer builds a crash title
3. Then the title is `[crash] a1b2c3d4e5f6 …` and a second search inside 60s does not fetch again

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../githubfeedback/` |
| Tests | `src/test/.../githubfeedback/` |
| Wiring | none (Feedback UI calls this) |
## Tests

- Automated: yes — `src/test/.../githubfeedback/`
- Coverage: title format, placeholder short-circuit, 60s search cooldown

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Own `isPlaceholderRepo` in this container (do not import About).
