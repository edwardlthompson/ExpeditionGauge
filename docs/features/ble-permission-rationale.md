# Feature: ble-permission-rationale

> Explain BLE scan as pairing-only, with no upload.

## Acceptance criteria

- ✅ Copy says pairing for IMU/TPMS/OBD/GPS
- ✅ Copy says nothing is uploaded
- ✅ i18n: `ble_permission_rationale`

## Smoke scenario

1. Given the permission prompt
2. When the rationale is shown
3. Then it states pairing-only and no upload

## Container map

| Layer | Path |
|-------|------|
| Logic | `blepermissionrationale/BlePermissionRationale.kt` |
| Tests | `app/src/test/.../blepermissionrationale/` |

## Tests

- Automated: yes — `BlePermissionRationaleTest`
- Coverage: pairing + no upload

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
