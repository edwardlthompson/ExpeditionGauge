# Feature: ambient-autodim

> Auto brightness uses the ambient light sensor instead of only the system override.

## Acceptance criteria

- ✅ Lux curve: 15 lx → 0.18, 600 lx → 0.95
- ✅ Auto + no lux reading → system brightness override
- ✅ Day / Night modes unchanged
- ✅ i18n: none (no new strings)

## Smoke scenario

1. Given brightness is Auto and the light sensor reports 10 lx
2. When the HUD is visible
3. Then window brightness is near the night floor

## Container map

| Layer | Path |
|-------|------|
| Logic | `ambient/AmbientAutodim.kt` |
| View | `ui/ambient/AmbientLux.kt` |
| Tests | `src/test/.../ambient/` |
| Wiring | `screenBrightnessFor` + Theme |

## Tests

- Automated: yes — `AmbientAutodimTest`
- Coverage: null lux; floor; ceiling; night flag

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
