# Feature: feedback

> About / Help review dialogs for bug and feature reports. Not a donate nag.

## Acceptance criteria

- 🔲 About has Report a bug and Request a feature; review panel shows escaped preview, Copy, Open GitHub, Discard
- 🔲 Copy still works offline; Open GitHub disabled with i18n reason when offline
- 🔲 Accessibility: dialog with labelled buttons; no Android Toast
- 🔲 i18n: `feedback_*`

## Smoke scenario

1. Given crash-capture is off
2. When the user opens About and Report a bug, types a description
3. Then they can copy sanitized markdown; Open GitHub is enabled only when description or stack exists

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../feedback/` |
| View | `examples/android/.../ui/feedback/` |
| Tests | `src/test/.../feedback/` |
| Wiring | `ExpeditionGaugeApp` ≤10 lines |
## Tests

- Automated: yes — `FeedbackPreviewTest.kt` (to add)
- Coverage: escaped preview; no `innerHTML` of reporter text

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Settings toggle “Save crash details for me to review” defaults off.
- Port patterns into new `feedback/` folders. Do not copy stub sources over existing About.
