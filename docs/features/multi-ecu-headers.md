# Feature: multi-ecu-headers

> Probe ISO-TP physical headers on the scan tick. Restore functional `7DF`.

## Acceptance criteria

- ✅ Catalog: `7DF` functional, `7E0` ECM, `7E1` TCM, `7E2` ABS, `7E3` 4WD
- ✅ `ATSH` + `0100` — `4100` means present; `NO DATA` skips
- ✅ HUD line `ECU 7E0 · 7E1` when a physical ECU answers
- ✅ Always restore `ATSH7DF` after the probe
- ✅ i18n: `multi_ecu_*`

## Smoke scenario

1. Given ECM `7E0` answers `0100`
2. When a DTC-scan tick runs
3. Then the phone HUD shows `ECU 7E0`

## Container map

| Layer | Path |
|-------|------|
| Logic | `multiecu/MultiEcuHeaders.kt` |
| Adapter | `obd/Elm327MultiEcu.kt` |
| View | `ui/multiecu/MultiEcuHeadersDialog.kt` + HUD line |
| Tests | `src/test/.../multiecu/` |
| Wiring | Settings + scan tick |

## Tests

- Automated: yes — `MultiEcuHeadersTest`
- Coverage: ATSH; 4100 vs NO DATA; functional-only hide

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
