# Feature: in-app-whats-new

> Show What’s new after a version bump and mark the current version seen.

## Acceptance criteria

- ✅ Shows when last seen is null or older than 2.18.12
- ✅ Hidden after the current version is marked seen
- ✅ About has a What’s new action
- ✅ i18n: `whats_new_title`

## Smoke scenario

1. Given a fresh install
2. When About What’s new is opened
3. Then privacy export and settings backup are listed and the version is marked seen

## Container map

| Layer | Path |
|-------|------|
| Logic | `inappwhatsnew/` |
| View | `ui/inappwhatsnew/WhatsNewButton.kt` |
| Tests | `app/src/test/.../inappwhatsnew/` |
| Wiring | `AboutScreen` |

## Tests

- Automated: yes — `WhatsNewTest`
- Coverage: shouldShow; body

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
