# Feature: phone-hud-dtc

> Phone Compose HUD shows stored DTCs with the same 5 s carousel as Android Auto ROW.

## Acceptance criteria

- ✅ When stored DTCs are non-empty, the telemetry cube shows a bold-red single-line carousel (`n/N` + code + title)
- ✅ Empty list hides the footer (no placeholder)
- ✅ Accessibility: line is the TalkBack description; no Toast
- ✅ i18n: `phone_hud_dtc_*`

## Smoke scenario

1. Given Mode 03/07 returned one or more codes
2. When the phone HUD is visible
3. Then a red footer cycles every 5 s and hides when the list is empty

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../phonehuddtc/PhoneHudDtc.kt` (uses `obd/dtc/DtcCarousel`) |
| View | `examples/android/.../ui/phonehuddtc/PhoneHudDtcFooter.kt` |
| Tests | `src/test/.../phonehuddtc/` |
| Wiring | `DashboardHudLayout` / `AppScreenDashboardRoute` ≤10 lines |

## Tests

- Automated: yes — `src/test/java/dev/foss/expeditiongauge/phonehuddtc/`
- Coverage: hide when empty; line matches carousel frame

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- AA ROW footer already uses `DtcCarousel`. Do not duplicate dwell math.
