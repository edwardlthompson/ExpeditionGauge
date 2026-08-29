# Feature: i18n-layout-stress

> Detect translations that overflow compact HUD and settings rows.

## Acceptance criteria

- ✅ HUD max 18 chars; row max 28
- ✅ Longest helper returns the longest string
- ✅ German sample overflows HUD
- ✅ i18n: none (checker)

## Smoke scenario

1. Given "Offline-Karten herunterladen"
2. When checked against HUD_MAX
3. Then overflows is true

## Container map

| Layer | Path |
|-------|------|
| Logic | `i18nlayoutstress/I18nLayoutStress.kt` |
| Tests | `app/src/test/.../i18nlayoutstress/` |

## Tests

- Automated: yes — `I18nLayoutStressTest`
- Coverage: German overflow

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
