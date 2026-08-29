# Feature: crash-capture

> Opt-in local crash queue. Never auto-sends. Sanitize before persist. Product already has `crash/CrashLogStore.kt`.

## Acceptance criteria

- ✅ After a captured crash, one review dialog; never auto-open GitHub
- ✅ Write failure drops the record; handler errors do not re-enter
- ✅ Save-crashes setting off: nothing persisted; turning it off deletes the queued record
- ✅ At most one sanitized record; allowlist keys only
- ✅ i18n: `feedback_*` / existing crash strings

## Smoke scenario

1. Given the save-crashes setting is off
2. When an unhandled error occurs
3. Then nothing is persisted
4. When the setting is on, at most one sanitized record is stored

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../crash/` (extend; do not add `goldenpath.crashcapture`) |
| Tests | `src/test/.../crash/` |
| Wiring | `ExpeditionGaugeApp` / `MainActivity` ≤10 lines |
## Tests

- Automated: yes — `CrashLogStoreTest.kt` plus new PendingCrash allowlist tests
- Coverage: queue-at-most-one, sanitize-before-persist, opt-in false

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Reference stub: `examples/android/.../goldenpath/crashcapture/` in the template repo only.
- After each AGENT step: `python3 scripts/agent-run.py watch-agent-gates --once --autofix`
