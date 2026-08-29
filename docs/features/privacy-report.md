# Feature: privacy-report

> Shared sanitizer, fingerprint, and markdown builder. No UI and no network.

## Acceptance criteria

- 🔲 `null`/empty input becomes `""`; size cap drops excess lines (8 KiB / 200 stack lines)
- 🔲 Paths, `ghp_` tokens, JWTs, and `AKIA` keys are stripped
- 🔲 Fingerprint is stable if only the username in a path changes

## Smoke scenario

1. Given a stack containing `C:\Users\Ada\secret.env`, a `ghp_` token, a JWT, and `AKIA`
2. When sanitize + markdown run
3. Then none of those secrets remain and the fingerprint is stable across username-only path changes

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../privacyreport/` plus `scripts/lib/privacy_report_*.py` |
| Tests | Android JUnit + `tests/privacy_report/` |
| Wiring | none |
## Tests

- Automated: yes — JUnit plus `tests/privacy_report/`
- Coverage: token/path redaction; fingerprint stability

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Run sanitize before persist and again before Copy / Open GitHub.
