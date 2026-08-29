# Feature: phone-imu-live

> Carry a second phone's IMU on the live channel as `imu|pitch|roll|hdg`.

## Acceptance criteria

- ✅ Encode/decode pipe format
- ✅ Merge overwrites pitch, roll, heading on the live sample
- ✅ Junk payloads return null
- ✅ i18n: `phone_imu_live`

## Smoke scenario

1. Given a second-phone IMU frame
2. When the pit receiver decodes it
3. Then the live sample heading matches the phone

## Container map

| Layer | Path |
|-------|------|
| Logic | `phoneimulive/PhoneImuLive.kt` |
| Tests | `app/src/test/.../phoneimulive/` |
| Wiring | Live sample merge helper |

## Tests

- Automated: yes — `PhoneImuLiveTest`
- Coverage: encode; merge; junk

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
