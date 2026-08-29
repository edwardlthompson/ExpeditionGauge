# Feature: dtc-full-title

> Tap the phone HUD DTC carousel to read the full OBDex title (carousel line may ellipsize).

## Acceptance criteria

- ✅ Tap the red footer opens a dialog with code + full catalog title
- ✅ Close dismisses; no network and no GitHub
- ✅ Empty list: no dialog
- ✅ i18n: `phone_hud_dtc_close`

## Smoke scenario

1. Given a long OBDex title is truncated on the HUD line
2. When the driver taps the footer
3. Then the dialog shows the complete title

## Container map

| Layer | Path |
|-------|------|
| Logic | `phonehuddtc/PhoneHudDtc.kt` |
| View | `ui/phonehuddtc/PhoneHudDtcFooter.kt` |
| Tests | `src/test/.../phonehuddtc/` |
| Wiring | none (extends existing footer) |

## Tests

- Automated: yes — `PhoneHudDtcTest.fullTitleKeepsUntruncatedDescription`
- Coverage: full title is code + description without ellipsis

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
