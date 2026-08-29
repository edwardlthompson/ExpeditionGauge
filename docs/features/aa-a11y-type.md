# Feature: aa-a11y-type

> Android Auto HUD type scales with the car UI font, and Pane/Grid text is TalkBack-spoken.

## Acceptance criteria

- ✅ Car `fontScale` (and optional large-text pref) scales HUD glyphs 1.0–1.5×
- ✅ Pane/Grid titles use `label, value` spoken phrases
- ✅ Empty/ZWSP values speak the label only
- ✅ i18n: none (host TalkBack uses the spoken English phrases)

## Smoke scenario

1. Given the head unit font scale is 1.3
2. When Drive opens
3. Then telemetry glyphs are larger and Pane alerts read as “Alert, Pitch”

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aaa11y/AaA11yType.kt` |
| View | Telemetry cube + Pane/Grid titles |
| Tests | `car/src/test/.../aaa11y/` |
| Wiring | `AaDisplaySpec.textScale` |

## Tests

- Automated: yes — `AaA11yTypeTest`
- Coverage: scale clamp; spoken empty/value

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
