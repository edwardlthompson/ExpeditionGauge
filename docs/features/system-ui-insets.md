# Feature: system-ui-insets

> Sprint 19b — navigation bar / gesture inset padding with `enableEdgeToEdge()`.

## Acceptance criteria

- Record controls, playback scrubber, and mark-event FAB sit above the system navigation bar (3-button and gesture).
- MapLibre playback map reserves bottom inset so attribution and route are not obscured.
- `ModalBottomSheet` instances respect navigation bar insets (`contentWindowInsets`).
- Status bar remains edge-to-edge; only selective `navigationBars` padding on bottom chrome.

## Screen audit checklist

| Screen / surface | Bottom chrome | Inset owner |
|------------------|---------------|-------------|
| Dashboard | `RecordControls`, `MarkEventFab` | Parallel agents A |
| Playback | `ScrubberMarkerStrip`, scrubber row | Parallel B + dock |
| Playback map | MapLibre padding | Parallel C |
| Attitude G-meter sheet | `ModalBottomSheet` | Parallel D |
| Recording advanced sheet | `ModalBottomSheet` | Parallel E |
| Settings, sessions, About | `InsetAwareScaffold` content | Sequential wire |
| Onboarding, permissions | Full-screen flows | Sequential wire |
| `LivePairingSheet` | Embedded column (no modal) | Audit only — parent padding |

## Container map

| Layer | Path |
|-------|------|
| Contract | `docs/features/system-ui-insets.md` |
| Scaffold | `ui/layout/InsetAwareScaffold.kt` |
| Wiring | `ui/navigation/ExpeditionGaugeApp.kt`, `AppScreenRouter.kt` |
| Bottom chrome | Parallel scopes in BUILD_PLAN Sprint 19b |

## Smoke scenario

1. Cold start on OnePlus 12 with 3-button nav — record button fully visible.
2. Switch to gesture nav — record + scrubber still clear after rotation.
3. Open attitude detail sheet — content scrolls above home indicator.
