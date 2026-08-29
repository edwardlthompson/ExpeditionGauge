# Feature: storage-meter

> Settings storage section shows a percent bar of used vs allowed session bytes.

## Acceptance criteria

- ✅ Percent is used/allowed, clamped 0–100
- ✅ Zero allowed cap reports 100%
- ✅ Progress bar is TalkBack-labelled
- ✅ i18n: `storage_meter_cd`

## Smoke scenario

1. Given Settings → Recording → Storage
2. When used and allowed bytes are known
3. Then a progress bar matches the percent used

## Container map

| Layer | Path |
|-------|------|
| Logic | `storagemeter/StorageMeter.kt` |
| View | `ui/storagemeter/StorageMeterBar.kt` |
| Tests | `app/src/test/.../storagemeter/` |
| Wiring | `SettingsStorageOptions` |

## Tests

- Automated: yes — `StorageMeterTest`
- Coverage: 0/50/over/zero-cap

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
